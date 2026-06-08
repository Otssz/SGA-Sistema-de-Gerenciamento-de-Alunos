package br.edu.sgaedu.dominio.command;

import br.edu.sgaedu.dominio.entidade.Aluno;
import br.edu.sgaedu.dominio.entidade.Matricula;
import br.edu.sgaedu.dominio.entidade.Turma;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.*;

@DisplayName("MatricularAlunoCommand — Command Pattern")
class MatricularAlunoCommandTest {

    private Aluno aluno;
    private Turma turma;
    private List<Matricula> bancoDeDados;

    @BeforeEach
    void setUp() {
        aluno = new Aluno();
        aluno.setNome("Ana Silva");

        turma = new Turma();
        turma.setCodigo("EM1A-2024");

        bancoDeDados = new ArrayList<>();
    }

    @Test
    @DisplayName("executar deve criar e salvar matrícula com status ATIVA")
    void executarDeveSalvarMatriculaAtiva() {
        Function<Matricula, Matricula> salvar = m -> {
            bancoDeDados.add(m);
            return m;
        };
        Consumer<Matricula> cancelar = m -> m.setStatus(Matricula.Status.CANCELADA);

        MatricularAlunoCommand comando = new MatricularAlunoCommand(aluno, turma, salvar, cancelar);
        comando.executar();

        assertThat(bancoDeDados).hasSize(1);
        assertThat(bancoDeDados.get(0).getStatus()).isEqualTo(Matricula.Status.ATIVA);
        assertThat(bancoDeDados.get(0).getAluno()).isSameAs(aluno);
        assertThat(bancoDeDados.get(0).getTurma()).isSameAs(turma);
    }

    @Test
    @DisplayName("executar deve preencher a data de efetivação com hoje")
    void executarDevePreencherDataEfetivacao() {
        MatricularAlunoCommand comando = new MatricularAlunoCommand(
                aluno, turma, m -> { bancoDeDados.add(m); return m; }, m -> {}
        );
        comando.executar();

        assertThat(bancoDeDados.get(0).getDataEfetivacao()).isNotNull();
    }

    @Test
    @DisplayName("desfazer deve cancelar a matrícula criada")
    void desfazerDeveCancelarMatricula() {
        List<Matricula> canceladas = new ArrayList<>();
        MatricularAlunoCommand comando = new MatricularAlunoCommand(
                aluno, turma, m -> { bancoDeDados.add(m); return m; }, canceladas::add
        );

        comando.executar();
        comando.desfazer();

        assertThat(canceladas).hasSize(1);
    }

    @Test
    @DisplayName("desfazer sem executar antes deve lançar IllegalStateException")
    void desfazerSemExecutarDeveLancarExcecao() {
        MatricularAlunoCommand comando = new MatricularAlunoCommand(
                aluno, turma, m -> m, m -> {}
        );

        assertThatThrownBy(comando::desfazer)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("não foi executado");
    }

    @Test
    @DisplayName("getMatriculaCriada deve retornar null antes de executar")
    void getMatriculaCriadaDeveSerNulaAntesDeExecutar() {
        MatricularAlunoCommand comando = new MatricularAlunoCommand(
                aluno, turma, m -> m, m -> {}
        );
        assertThat(comando.getMatriculaCriada()).isNull();
    }
}
