# Clyvo Companion

API REST desenvolvida em **Java 17** e **Spring Boot** para o challenge **Java Advanced (FIAP)**. O sistema apoia tutores no acompanhamento da saúde e dos cuidados de pets: cadastro de animais, métricas de saúde, prescrições, agendamentos em clínicas e auditoria de erros da aplicação.

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
| Controller | `controller` | Endpoints REST |
| DTO | `dto` | Contratos de entrada e saída da API |
| Exception | `exception` | Tratamento centralizado de erros |

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

- Java 17
- Spring Boot 4
- Spring Data JPA
- Bean Validation
- SpringDoc OpenAPI (Swagger)
- Oracle Database (FIAP) / H2 (desenvolvimento)
- Maven

## Como executar

### Pré-requisitos

- JDK 17+
- Maven Wrapper incluído no projeto (`mvnw`)

### Oracle (ambiente FIAP)

Configuração padrão em `application-oracle.properties`:

| Parâmetro | Valor |
|-----------|-------|
| Host | oracle.fiap.com.br |
| Porta | 1521 |
| SID | ORCL |
| Usuário | rm563960 |

A senha deve ser informada em `application-local.properties` (a partir de `application-local.properties.example`) ou pela variável de ambiente `DB_PASSWORD`.

```powershell
.\mvnw.cmd spring-boot:run
```

### H2 (desenvolvimento local)

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

### Acesso à aplicação

| Recurso | URL |
|---------|-----|
| API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| H2 Console (perfil dev) | http://localhost:8080/h2-console |

## API REST

### Recursos (CRUD)

| Recurso | Endpoint base |
|---------|----------------|
| Usuários | `/usuarios` |
| Pets | `/pets` |
| Clínicas | `/clinicas` |
| Prescrições | `/prescricoes` |
| Logs de saúde | `/logs-saude` |
| Agendamentos | `/agendamentos` |
| Logs de sistema | `/logs-sistema` |

Operações disponíveis: `GET` (listagem paginada e por ID), `POST`, `PUT` e `DELETE`, conforme o recurso.

### Regras de negócio

| Método | Endpoint | Descrição |
|--------|----------|-----------|
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

## Recursos implementados (1ª Sprint)

- Persistência relacional com JPA e mapeamento alinhado ao modelo de dados
- API REST com DTOs e Bean Validation
- Paginação, ordenação e consultas parametrizadas (JPQL)
- Cache de leitura com Spring Cache
- Documentação da API via Swagger
- Tratamento global de exceções com respostas padronizadas
- Funcionalidades de negócio além do CRUD básico
