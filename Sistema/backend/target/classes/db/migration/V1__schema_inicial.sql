-- =================================================================
-- SGA-Edu — Schema inicial
-- Flyway executa este arquivo automaticamente ao subir a aplicação
-- =================================================================

-- Tabela pai da hierarquia de usuários
CREATE TABLE usuarios (
    id         BIGSERIAL PRIMARY KEY,
    papel      VARCHAR(20)  NOT NULL,   -- discriminator: ALUNO|PROFESSOR|SECRETARIA|DIRETOR
    nome       VARCHAR(150) NOT NULL,
    email      VARCHAR(200) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    ativo      BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_em  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE alunos (
    usuario_id       BIGINT PRIMARY KEY REFERENCES usuarios(id),
    matricula        VARCHAR(20)  NOT NULL UNIQUE,
    data_nascimento  DATE         NOT NULL,
    cpf              VARCHAR(14)
);

CREATE TABLE professores (
    usuario_id   BIGINT PRIMARY KEY REFERENCES usuarios(id),
    especialidade VARCHAR(100) NOT NULL,
    registro      VARCHAR(20)  NOT NULL UNIQUE
);

CREATE TABLE secretarias (
    usuario_id BIGINT PRIMARY KEY REFERENCES usuarios(id),
    ramal      VARCHAR(20) NOT NULL
);

CREATE TABLE diretores (
    usuario_id   BIGINT PRIMARY KEY REFERENCES usuarios(id),
    departamento VARCHAR(100) NOT NULL
);

CREATE TABLE responsaveis (
    id         BIGSERIAL PRIMARY KEY,
    nome       VARCHAR(150) NOT NULL,
    email      VARCHAR(200) NOT NULL UNIQUE,
    telefone   VARCHAR(20),
    parentesco VARCHAR(50)  NOT NULL
);

CREATE TABLE aluno_responsavel (
    aluno_id       BIGINT REFERENCES alunos(usuario_id),
    responsavel_id BIGINT REFERENCES responsaveis(id),
    PRIMARY KEY (aluno_id, responsavel_id)
);

CREATE TABLE cursos (
    id                  BIGSERIAL PRIMARY KEY,
    nome                VARCHAR(100) NOT NULL,
    descricao           VARCHAR(500),
    carga_horaria_total INT          NOT NULL
);

CREATE TABLE turmas (
    id         BIGSERIAL PRIMARY KEY,
    codigo     VARCHAR(20) NOT NULL UNIQUE,
    ano_letivo VARCHAR(10) NOT NULL,
    turno      VARCHAR(20) NOT NULL,
    curso_id   BIGINT      NOT NULL REFERENCES cursos(id)
);

CREATE TABLE professor_turma (
    professor_id BIGINT REFERENCES professores(usuario_id),
    turma_id     BIGINT REFERENCES turmas(id),
    PRIMARY KEY (professor_id, turma_id)
);

CREATE TABLE avaliacoes (
    id              BIGSERIAL PRIMARY KEY,
    descricao       VARCHAR(100) NOT NULL,
    data_aplicacao  DATE         NOT NULL,
    peso            NUMERIC(4,2) NOT NULL DEFAULT 1,
    valor_maximo    NUMERIC(4,2) NOT NULL DEFAULT 10,
    turma_id        BIGINT       NOT NULL REFERENCES turmas(id)
);

CREATE TABLE notas (
    id            BIGSERIAL PRIMARY KEY,
    valor         NUMERIC(4,2) NOT NULL CHECK (valor >= 0 AND valor <= 10),
    observacao    VARCHAR(500),
    lancada_em    TIMESTAMP    NOT NULL DEFAULT NOW(),
    aluno_id      BIGINT       NOT NULL REFERENCES alunos(usuario_id),
    avaliacao_id  BIGINT       NOT NULL REFERENCES avaliacoes(id),
    professor_id  BIGINT       NOT NULL REFERENCES professores(usuario_id),
    UNIQUE (aluno_id, avaliacao_id)
);

CREATE TABLE matriculas (
    id                 BIGSERIAL PRIMARY KEY,
    status             VARCHAR(20) NOT NULL DEFAULT 'ATIVA',
    data_efetivacao    DATE        NOT NULL,
    data_cancelamento  DATE,
    motivo_cancelamento VARCHAR(300),
    criada_em          TIMESTAMP   NOT NULL DEFAULT NOW(),
    aluno_id           BIGINT      NOT NULL REFERENCES alunos(usuario_id),
    turma_id           BIGINT      NOT NULL REFERENCES turmas(id),
    UNIQUE (aluno_id, turma_id)
);

CREATE TABLE logs_auditoria (
    id            BIGSERIAL PRIMARY KEY,
    acao          VARCHAR(50)  NOT NULL,
    entidade      VARCHAR(50)  NOT NULL,
    entidade_id   BIGINT,
    executado_por VARCHAR(200) NOT NULL,
    endereco_ip   VARCHAR(45),
    detalhes      TEXT,
    ocorrido_em   TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Índices para consultas frequentes
CREATE INDEX idx_notas_aluno_avaliacao ON notas(aluno_id, avaliacao_id);
CREATE INDEX idx_matriculas_aluno      ON matriculas(aluno_id);
CREATE INDEX idx_matriculas_turma      ON matriculas(turma_id, status);
CREATE INDEX idx_logs_executado_por    ON logs_auditoria(executado_por);
CREATE INDEX idx_logs_ocorrido_em      ON logs_auditoria(ocorrido_em);
