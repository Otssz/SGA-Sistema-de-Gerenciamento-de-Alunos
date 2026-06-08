-- =================================================================
-- SGA-Edu — Dados iniciais
--
-- Os usuários de teste são criados pelo InicializadorDados.java
-- ao subir a aplicação (perfil dev).
--
-- Credenciais:
--   diretor@escola.edu     / Senha@123
--   secretaria@escola.edu  / Senha@123
--   professor@escola.edu   / Senha@123
--   aluno@escola.edu       / Senha@123
--
-- Curso e turma de exemplo para testes manuais:
-- =================================================================

INSERT INTO cursos (nome, descricao, carga_horaria_total)
VALUES ('Ensino Médio 1º Ano', 'Primeiro ano do ensino médio', 800);

INSERT INTO turmas (codigo, ano_letivo, turno, curso_id)
VALUES ('EM1A-2024', '2024', 'Manhã', 1);

INSERT INTO avaliacoes (descricao, data_aplicacao, peso, valor_maximo, turma_id)
VALUES ('Prova 1 - Álgebra', '2024-04-10', 2.0, 10.0, 1);
