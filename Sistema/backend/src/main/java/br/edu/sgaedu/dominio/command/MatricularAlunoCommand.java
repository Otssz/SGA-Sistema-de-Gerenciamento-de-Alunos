package br.edu.sgaedu.dominio.command;

import br.edu.sgaedu.dominio.entidade.Aluno;
import br.edu.sgaedu.dominio.entidade.Matricula;
import br.edu.sgaedu.dominio.entidade.Turma;
import java.time.LocalDate;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Comando Concreto — encapsula a matrícula de um aluno em uma turma (UC-01).
 *
 * Receptor: o repositório de matrículas é injetado via construtor como callbacks
 * funcionais, mantendo o domínio desacoplado da infraestrutura.
 */
public class MatricularAlunoCommand implements Comando {

    private final Aluno aluno;
    private final Turma turma;
    private final Function<Matricula, Matricula> salvarMatricula;
    private final Consumer<Matricula> cancelarMatricula;

    /** Matrícula criada por executar() — armazenada para desfazer(). */
    private Matricula matriculaCriada;

    /**
     * @param aluno            aluno a ser matriculado
     * @param turma            turma de destino
     * @param salvarMatricula  função que persiste a matrícula e retorna a entidade salva
     * @param cancelarMatricula consumer que cancela a matrícula para desfazer()
     */
    public MatricularAlunoCommand(Aluno aluno,
                                   Turma turma,
                                   Function<Matricula, Matricula> salvarMatricula,
                                   Consumer<Matricula> cancelarMatricula) {
        this.aluno = aluno;
        this.turma = turma;
        this.salvarMatricula = salvarMatricula;
        this.cancelarMatricula = cancelarMatricula;
    }

    @Override
    public void executar() {
        Matricula matricula = new Matricula();
        matricula.setAluno(aluno);
        matricula.setTurma(turma);
        matricula.setStatus(Matricula.Status.ATIVA);
        matricula.setDataEfetivacao(LocalDate.now());
        matriculaCriada = salvarMatricula.apply(matricula);
    }

    /** Cancela a matrícula criada por executar(). */
    @Override
    public void desfazer() {
        if (matriculaCriada == null) {
            throw new IllegalStateException("Comando não foi executado — nada a desfazer.");
        }
        cancelarMatricula.accept(matriculaCriada);
        matriculaCriada = null;
    }

    public Matricula getMatriculaCriada() { return matriculaCriada; }
}
