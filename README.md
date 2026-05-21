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
- [x] Bean Validation nos DTOs
- [x] Paginação e ordenação (`Pageable`)
- [x] Busca parametrizada (JPQL em `PetRepository`)
- [x] Cache (`@Cacheable` / `@CacheEvict`)
- [x] Tratamento de exceções (`GlobalExceptionHandler`)
- [x] DTOs
- [x] Swagger UI (`/swagger-ui.html`)
- [ ] Demais recursos REST (Usuario, Clinica, etc.) — próximo passo
- [ ] Coleção Postman/Insomnia em `documentos/`

## Como executar

### Desenvolvimento local (H2)

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

- API: http://localhost:8080/api/v1/pets
- Swagger: http://localhost:8080/swagger-ui.html
- H2 Console: http://localhost:8080/h2-console

### Oracle (banco FIAP)

1. Crie as tabelas conforme seu ERD.
2. Execute `sql/oracle-sequences.sql` se necessário.
3. Configure variáveis de ambiente:

```bash
set SPRING_PROFILES_ACTIVE=oracle
set DB_URL=jdbc:oracle:thin:@HOST:1521/SERVICE
set DB_USER=seu_usuario
set DB_PASSWORD=sua_senha
./mvnw spring-boot:run
```

## Endpoints — Pets

| Método | URL | Descrição |
|--------|-----|-----------|
| GET | `/api/v1/pets?page=0&size=10&sort=nomePet,asc` | Lista com paginação |
| GET | `/api/v1/pets?nome=thor&especie=cachorro&idUsuario=1` | Busca parametrizada |
| GET | `/api/v1/pets/{id}` | Busca por ID |
| POST | `/api/v1/pets` | Cria pet |
| PUT | `/api/v1/pets/{id}` | Atualiza pet |
| DELETE | `/api/v1/pets/{id}` | Remove pet |

### Exemplo POST

```json
{
  "idUsuario": 1,
  "nomePet": "Luna",
  "especie": "Gato",
  "dtNascimento": "2022-03-15"
}
```

## Documentação e entrega

Coloque na pasta `documentos/`:

- Export da coleção Postman ou Insomnia
- Prints ou relatório de testes
- Cronograma da equipe (se aplicável)

## Equipe

Atualize com os integrantes e o link do repositório público no GitHub.
