# Clyvo Companion

API REST em **Java 17** + **Spring Boot** para o challenge FIAP (Java Advanced) — gestão de saúde e cuidados de pets.

## Arquitetura

| Camada | Pacote | Responsabilidade |
|--------|--------|------------------|
| Model | `model` | Entidades JPA mapeadas ao ERD (`TB_CC_*`) |
| Repository | `repository` | Spring Data JPA (JPQL + Query Methods) |
| Service | `service` | Regras de negócio, cache, transações |
| Controller | `controller` | Endpoints REST |
| DTO | `dto` | Contratos de entrada/saída da API |
| Exception | `exception` | Tratamento global de erros |

### Entidades (Astah + ERD)

- `Usuario` → `TB_CC_USUARIO` (1:N `Pet`)
- `Pet` → `TB_CC_PET`
- `Prescricao` → `TB_CC_PRESCRICAO`
- `LogSaude` → `TB_CC_LOG_SAUDE`
- `Clinica` → `TB_CC_CLINICA`
- `Agendamento` → `TB_CC_AGENDAMENTO`
- `LogSistema` → `TB_CC_LOG_SISTEMA` (auditoria isolada)

## Requisitos atendidos (1ª Sprint)

- [x] Spring Boot + JPA com relacionamentos
- [x] Bean Validation nos DTOs (classes Java, sem `record`)
- [x] Paginação e ordenação (`Pageable`)
- [x] Busca parametrizada (JPQL nos repositories)
- [x] Cache (`@Cacheable` / `@CacheEvict`)
- [x] Tratamento de exceções (`GlobalExceptionHandler`)
- [x] DTOs em classes (`getter`/`setter` + método `from`)
- [x] Swagger UI (`/swagger-ui.html`)
- [x] API REST completa para todas as entidades
- [x] Coleção Postman em `postman/clyvo-companion.postman_collection.json`
- [x] Cronograma em `documentos/CRONOGRAMA.md`

## Como executar

### Desenvolvimento local (H2)

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

- API: http://localhost:8080
- Swagger: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console

### Oracle (banco FIAP)

Conexão configurada para:

- **Host:** `oracle.fiap.com.br`
- **Porta:** `1521`
- **SID:** `ORCL`
- **Usuário:** `rm563960`

1. Crie as tabelas e sequences conforme seu ERD (já existentes no banco FIAP).
2. Configure a senha de uma das formas:
   - Copie `application-local.properties.example` para `application-local.properties` e coloque sua senha (arquivo ignorado pelo Git).
   - Ou defina a variável de ambiente `DB_PASSWORD`.

```powershell
.\mvnw.cmd spring-boot:run
```

O perfil padrão já é `oracle`. Para H2 local: `-Dspring-boot.run.profiles=dev`

> **Importante:** não commite senha no GitHub público. O arquivo `application-local.properties` está no `.gitignore`.

## Endpoints REST

Todos os recursos seguem o padrão: `GET` (lista e por id), `POST`, `PUT`, `DELETE` (exceto `logs-sistema`, somente leitura/criação/exclusão).

| Recurso | Base URL |
|---------|----------|
| Usuários | `usuarios` |
| Pets | `pets` |
| Clínicas | `clinicas` |
| Prescrições | `prescricoes` |
| Logs de saúde | `logs-saude` |
| Agendamentos | `agendamentos` |
| Logs de sistema | `logs-sistema` |

### Filtros de busca (query params)

| Recurso | Parâmetros |
|---------|------------|
| Usuários | `nome`, `email`, `tpPerfil` |
| Pets | `nome`, `especie`, `idUsuario` |
| Clínicas | `nome`, `cnpj` |
| Prescrições | `idPet`, `medicamento` |
| Logs saúde | `idPet`, `metrica` |
| Agendamentos | `idPet`, `idClinica`, `status` |
| Logs sistema | `nomeProc`, `cdErro` |

### Paginação e ordenação

```
?page=0&size=10&sort=nomePet,asc
```

## Documentação e entrega

| Material | Onde está |
|----------|-----------|
| **Cronograma da equipe** | [`documentos/CRONOGRAMA.md`](documentos/CRONOGRAMA.md) |
| Arquitetura, diagramas, testes e prints | Documento **Word** da equipe |
| Coleção Postman | [`postman/clyvo-companion.postman_collection.json`](postman/clyvo-companion.postman_collection.json) |

### Endpoints de negócio (além do CRUD)

| Método | Endpoint |
|--------|----------|
| GET | `/pets/{id}/resumo-saude` |
| GET | `/prescricoes/ativas?idPet=` |
| PATCH | `/agendamentos/{id}/status` |
| GET | `/logs-saude/alertas?idPet=` |
| — | Log automático em `TB_CC_LOG_SISTEMA` (exceções da API) |

## Equipe

| RM | Nome | Papel |
|----|------|-------|
| rm563960 | Lucas Silva Gastão Pinheiro | Persistência e modelagem |
| rm562673 | Guilherme Soares De Almeida | API REST |
| rm563143 | Geovanne Coneglian Passos | Negócio e qualidade |

Cronograma detalhado: [`documentos/CRONOGRAMA.md`](documentos/CRONOGRAMA.md)

**GitHub:** `https://github.com/SEU_USUARIO/clyvo-companion` _(link público)_
