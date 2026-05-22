# Cronograma de desenvolvimento — 1ª Sprint

**Projeto:** Clyvo Companion  
**Disciplina:** Java Advanced — FIAP  

> A documentação completa (arquitetura, diagramas, testes e prints) está no material em **Word** da equipe.  
> Este repositório mantém aqui apenas o **cronograma**, conforme exigência da entrega.

## Integrantes

| RM | Nome | Papel no projeto |
|----|------|------------------|
| rm563960 | Lucas Silva Gastão Pinheiro | **Persistência e modelagem** — ERD, Oracle, entidades JPA e relacionamentos |
| rm562673 | Guilherme Soares De Almeida | **API REST** — Controllers, DTOs, validação, Swagger, cache e CRUD |
| rm563143 | Geovanne Coneglian Passos | **Negócio e qualidade** — Regras além do CRUD, testes Postman e documentação |

## Distribuição de atividades

Cada integrante ficou responsável por **três frentes principais**, com apoio mútuo em modelagem inicial e entrega final.

| # | Atividade | Responsável | Início | Fim | Status |
|---|-----------|-------------|--------|-----|--------|
| 1 | Modelagem de classes (Astah) e DER Oracle | **Equipe** | 08/05/2026 | 12/05/2026 | Concluído |
| 2 | Criação de tabelas e sequences no Oracle FIAP | **Lucas** | 12/05/2026 | 14/05/2026 | Concluído |
| 3 | Implementação das entidades JPA e relacionamentos | **Lucas** | 14/05/2026 | 18/05/2026 | Concluído |
| 4 | Repositories, Services e consultas JPQL (base CRUD) | **Guilherme** | 15/05/2026 | 20/05/2026 | Concluído |
| 5 | Controllers REST, DTOs, Bean Validation, Swagger e cache | **Guilherme** | 18/05/2026 | 22/05/2026 | Concluído |
| 6 | Endpoints de negócio (resumo pet, prescrições ativas, status, alertas) | **Geovanne** | 20/05/2026 | 24/05/2026 | Concluído |
| 7 | Tratamento de exceções e log automático em `LogSistema` | **Geovanne** | 22/05/2026 | 25/05/2026 | Concluído |
| 8 | Coleção Postman, testes de API e documentação Word | **Geovanne** | 24/05/2026 | 28/05/2026 | Em andamento |
| 9 | Repositório público GitHub e revisão geral da sprint | **Equipe** | 26/05/2026 | 30/05/2026 | Em andamento |

## Resumo por integrante

### Lucas Silva Gastão Pinheiro (rm563960)
- Modelagem de dados em conjunto com a equipe  
- Scripts e persistência no Oracle  
- Entidades JPA alinhadas ao Astah e ao ERD  

### Guilherme Soares De Almeida (rm562673)
- Camada de serviço e repositórios Spring Data  
- Exposição REST completa (CRUD de todos os recursos)  
- Validação de entrada, documentação Swagger e cache  

### Geovanne Coneglian Passos (rm563143)
- Regras de negócio que vão além do CRUD simples  
- Auditoria de erros (`LogSistema`) e validações de fluxo  
- Testes com Postman e montagem da documentação de entrega  

## Repositório

`https://github.com/SEU_USUARIO/clyvo-companion` _(substituir pelo link real do GitHub público)_
