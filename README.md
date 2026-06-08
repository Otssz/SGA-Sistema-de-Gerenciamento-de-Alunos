# SGA-Edu — Sistema de Gestão Acadêmica Escolar

## Stack

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 17 |
| Framework | Spring Boot 3.2 |
| Segurança | Spring Security + JWT (jjwt 0.12) |
| ORM | Spring Data JPA + Hibernate |
| Banco | PostgreSQL 16 |
| Migrations | Flyway |
| PDF | iTextPDF 5 |
| Testes | JUnit 5 + Mockito + AssertJ |
| Cobertura | JaCoCo (mínimo 60%) |
| Build | Maven 3.9 |
| Container | Docker + Docker Compose |

## Pré-requisitos

- Java 17+
- Maven 3.9+ (ou use o wrapper `./mvnw`)
- Docker + Docker Compose (para rodar com banco)

---

## Subindo com Docker (recomendado para primeiros testes)

```bash
# Clone o repositório
git clone <url-do-repo>
cd sga-edu

# Copie o .env de exemplo e ajuste as variáveis
cp .env.example .env

# Suba os containers (PostgreSQL + Backend)
docker compose up --build

# A API estará disponível em: http://localhost:8080/api/v1
```

---

## Desenvolvimento local (sem Docker)

### 1. Banco de dados

Instale PostgreSQL 16 e crie o banco:

```sql
CREATE USER sgaedu WITH PASSWORD 'sgaedu';
CREATE DATABASE sgaedu OWNER sgaedu;
```

### 2. Variáveis de ambiente

Defina as variáveis ou edite `application-dev.properties`:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/sgaedu
export DB_USER=sgaedu
export DB_PASS=sgaedu
export JWT_SECRET=dev_secret_local_32_caracteres_minimo
export JWT_EXP_MS=3600000
export STORAGE_DIR=/tmp/sgaedu/documentos
```

### 3. Compilar e rodar

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## Rodar os testes

```bash
cd backend

# Todos os testes
mvn test

# Com relatório de cobertura (target/site/jacoco/index.html)
mvn verify

# Somente testes unitários de domínio
mvn test -Dtest="br.edu.sgaedu.dominio.*"
```

---

## Estrutura de pacotes

```
br.edu.sgaedu
├── dominio/
│   ├── entidade/       ← Entidades JPA — sem imports de Spring/web
│   ├── strategy/       ← GoF Strategy: MediaAritmetica, Ponderada, PorCompetencias
│   ├── fabrica/        ← GoF Factory Method: DocumentoFactory, BoletimFactory, HistoricoFactory
│   ├── documento/      ← Produto abstrato: Documento, Boletim, Historico
│   ├── observer/       ← GoF Observer: eventos + observadores
│   └── command/        ← GoF Command: Matricular, Cancelar, Invoker
├── aplicacao/          ← @Service — orquestram casos de uso (UC-01 a UC-05)
├── api/
│   ├── controller/     ← @RestController — sem lógica de negócio
│   └── dto/            ← Records de entrada/saída
└── infraestrutura/
    ├── persistencia/   ← JpaRepository interfaces
    └── seguranca/      ← JWT, filtros, Spring Security config
```

---

## Regras arquiteturais obrigatórias

1. **Domínio não importa Spring** — nenhum `@Service`, `@Autowired`, `@Repository` em `dominio/`
2. **Controllers não têm lógica** — delegam 100% para `aplicacao/`
3. **Todas as operações sensíveis passam pelo AuditoriaService** — LGPD
4. **Injeção via construtor** — sem `new` nas camadas superiores
5. **Nomes em português** — classes, métodos e variáveis (exceto termos técnicos: DTO, JWT, etc.)

---

## Endpoints principais

| Método | Rota | Papel | Descrição |
|---|---|---|---|
| POST | `/auth/login` | Público | Autentica e retorna JWT |
| POST | `/matriculas` | SECRETARIA, DIRETOR | Matricula aluno (UC-01) |
| DELETE | `/matriculas/{id}` | SECRETARIA, DIRETOR | Cancela matrícula |
| POST | `/turmas/{id}/notas` | PROFESSOR | Lança nota (UC-02) |
| GET | `/turmas/{id}/notas` | PROFESSOR, ALUNO | Lista notas |
| GET | `/documentos/boletim` | SECRETARIA, ALUNO | Gera boletim PDF (UC-04) |
| GET | `/documentos/historico` | SECRETARIA, ALUNO | Gera histórico PDF (UC-04) |

---

## Próximos passos

- [ ] Criar migrations Flyway em `src/main/resources/db/migration/`
- [ ] Implementar `gerarPdf()` em `Boletim` e `Historico` (iTextPDF)
- [ ] Implementar observadores: `ObservadorEmail` e `ObservadorPush`
- [ ] Implementar `MediaService.verificarRiscoReprovacaoGlobal()`
- [ ] Adicionar endpoint de frequências (UC-05)
- [ ] Configurar Flyway com schema inicial
- [ ] Adicionar tratamento global de exceções (`@ControllerAdvice`)
- [ ] Documentar API com Swagger/OpenAPI (`springdoc-openapi`)
- [ ] Pipeline CI/CD (GitHub Actions)

---

## Segurança e LGPD

- Senhas armazenadas com **Argon2id** (nunca em texto puro)
- Toda operação em dados pessoais gera **LogAuditoria** (imutável)
- Tabela `logs_auditoria` é **append-only** — nunca deletar registros
- JWT expira em 1 hora — renove via novo login
- HTTPS obrigatório em produção (configure no load balancer/reverse proxy)
