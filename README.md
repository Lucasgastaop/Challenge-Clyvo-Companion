# Clyvo Companion (Clyvo Care)

Aplicação web em **Java 17** e **Spring Boot** para o challenge **Java Advanced (FIAP)**. O Clyvo Care apoia tutores e veterinários no acompanhamento da saúde de pets: cadastro de animais, logs de saúde, prescrições, agendamentos e auditoria.

**Repositório:** https://github.com/Lucasgastaop/Challenge-Clyvo-Companion

## Equipe

| RM | Nome | Papel |
|----|------|-------|
| rm563960 | Lucas Silva Gastão Pinheiro | Persistência e modelagem |
| rm562673 | Guilherme Soares De Almeida | API REST |
| rm563143 | Geovanne Coneglian Passos | Negócio e qualidade |

Cronograma de atividades: [`documentos/CRONOGRAMA.md`](documentos/CRONOGRAMA.md)

## Arquitetura

| Camada | Pacote | Responsabilidade |
|--------|--------|------------------|
| Model | `model` | Entidades JPA mapeadas ao ERD (`TB_CC_*`) |
| Repository | `repository` | Spring Data JPA (JPQL e Query Methods) |
| Service | `service` | Regras de negócio, cache e transações |
| Security | `security` | Autenticação, papéis e política de acesso ao pet |
| Controller REST | `controller` | Endpoints JSON |
| Controller Web | `controller.web` | Telas Thymeleaf |
| DTO | `dto` | Contratos de entrada/saída com Bean Validation |
| Exception | `exception` | Tratamento centralizado de erros |
| Migrações | `db/migration` | Versionamento do schema com Flyway |

### Entidades

| Classe | Tabela | Relacionamento principal |
|--------|--------|---------------------------|
| Usuario | TB_CC_USUARIO | 1:N Pet |
| Pet | TB_CC_PET | N:1 Usuario |
| Prescricao | TB_CC_PRESCRICAO | N:1 Pet |
| LogSaude | TB_CC_LOG_SAUDE | N:1 Pet |
| Clinica | TB_CC_CLINICA | 1:N Agendamento |
| Agendamento | TB_CC_AGENDAMENTO | N:1 Pet, N:1 Clinica |
| LogSistema | TB_CC_LOG_SISTEMA | Auditoria (isolada) |

## Tecnologias

- Java 17 e Spring Boot 4
- Spring Data JPA + Flyway
- Spring Security (form login + HTTP Basic)
- Thymeleaf + HTML5/CSS3
- Bean Validation
- SpringDoc OpenAPI (Swagger)
- Oracle Database (FIAP) / H2 (desenvolvimento)
- Maven

## Como executar

### Pré-requisitos

- JDK 17+
- Maven Wrapper incluído no projeto (`mvnw`)

### H2 (recomendado para demonstração local)

Cria as tabelas e os dados iniciais automaticamente via Flyway (`V1` e `V2`).

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

Abra http://localhost:8080/login

| Perfil | E-mail | Senha |
|--------|--------|-------|
| Tutor | `maria@email.com` | `senha123` |
| Veterinário | `carlos.vet@clyvo.com` | `senha123` |

As senhas da carga inicial são recodificadas para **BCrypt** na primeira subida.

### Oracle (ambiente FIAP)

Configuração em `application-oracle.properties`:

| Parâmetro | Valor |
|-----------|-------|
| Host | oracle.fiap.com.br |
| Porta | 1521 |
| SID | ORCL |
| Usuário | rm563960 |

O Flyway executa as mesmas migrações. Se o schema já tiver as tabelas da sprint anterior, esvazie-o antes da primeira execução (o `V1` faz `CREATE TABLE`).

```powershell
.\mvnw.cmd spring-boot:run
```

### Acesso

| Recurso | URL |
|---------|-----|
| Aplicação (login) | http://localhost:8080/login |
| Painel | http://localhost:8080/ |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| H2 Console (perfil `dev`) | http://localhost:8080/h2-console |

A API REST continua disponível e exige autenticação (sessão após o login ou HTTP Basic com o mesmo e-mail/senha).

## Camada web (3ª Sprint)

| Rota | Perfil | Função |
|------|--------|--------|
| `/tutor/pets` | TUTOR | Cadastro de pets |
| `/tutor/logs-saude` | TUTOR | Fluxo 1 — registrar log de saúde |
| `/vet/consultas` | VETERINARIO | Agenda e origem da prescrição |
| `/vet/prescricoes` | VETERINARIO | Fluxo 2 — emitir prescrição médica |

Validações dos fluxos: Bean Validation nos DTOs + regras de limite de métrica (`MetricaSaudeValidator`) e período da prescrição.

## API REST

### Recursos (CRUD)

| Recurso | Endpoint base |
|---------|----------------|
| Autenticação | `/auth` |
| Usuários | `/usuarios` |
| Pets | `/pets` |
| Clínicas | `/clinicas` |
| Prescrições | `/prescricoes` |
| Logs de saúde | `/logs-saude` |
| Agendamentos | `/agendamentos` |
| Logs de sistema | `/logs-sistema` |

Operações disponíveis: `GET` (listagem paginada e por ID), `POST`, `PUT` e `DELETE`, conforme o recurso.

`POST` de log de saúde é exclusivo do tutor; `POST` de prescrição é exclusivo do veterinário.

### Regras de negócio

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/auth/login` | Autentica com e-mail e senha e abre a sessão |
| GET | `/auth/me` | Retorna o usuário autenticado |
| POST | `/auth/logout` | Encerra a sessão |
| GET | `/pets/{id}/resumo-saude` | Consolida prescrições ativas, últimos logs e próximo agendamento |
| GET | `/prescricoes/ativas?idPet={id}` | Lista prescrições em vigor |
| PATCH | `/agendamentos/{id}/status` | Atualiza status (`AGENDADO` → `CONCLUIDO` ou `CANCELADO`) |
| GET | `/logs-saude/alertas?idPet={id}` | Retorna métricas fora dos limites de referência |
| — | Tratamento de exceções | Registro automático em `TB_CC_LOG_SISTEMA` |

### Busca, paginação e ordenação

Exemplo:

```
GET /pets?nome=thor&page=0&size=10&sort=nomePet,asc
```

| Recurso | Parâmetros de filtro |
|---------|----------------------|
| Usuários | `nome`, `email`, `tpPerfil` |
| Pets | `nome`, `especie`, `idUsuario` |
| Clínicas | `nome`, `cnpj` |
| Prescrições | `idPet`, `medicamento` |
| Logs de saúde | `idPet`, `metrica` |
| Agendamentos | `idPet`, `idClinica`, `status` |
| Logs de sistema | `nomeProc`, `cdErro` |

## Documentação complementar

| Material | Localização |
|----------|-------------|
| Cronograma da sprint | [`documentos/CRONOGRAMA.md`](documentos/CRONOGRAMA.md) |
| Diagramas, arquitetura e relatório de testes | Documento Word (entrega da equipe) |
| Coleção Postman (entrega) | [`documentos/clyvo-companion.postman_collection.json`](documentos/clyvo-companion.postman_collection.json) — **18 requisições** numeradas (01–18); importar e rodar no Collection Runner com a API ativa |

## Recursos implementados (3ª Sprint)

- Versionamento do banco com Flyway (`V1__create_tables.sql` e `V2__insert_initial_data.sql`)
- Spring Security com papéis `ROLE_TUTOR` e `ROLE_VETERINARIO`
- Camada de visualização Thymeleaf com navegação por perfil
- Fluxo do tutor: cadastro de pet e log de saúde com limites de métrica
- Fluxo do veterinário: consulta da agenda e emissão de prescrição
- Senhas persistidas com BCrypt

## Recursos implementados (1ª Sprint)

- Persistência relacional com JPA e mapeamento alinhado ao modelo de dados
- API REST com DTOs e Bean Validation
- Paginação, ordenação e consultas parametrizadas (JPQL)
- Cache de leitura com Spring Cache
- Documentação da API via Swagger
- Tratamento global de exceções com respostas padronizadas
- Funcionalidades de negócio além do CRUD básico
