package br.edu.sgaedu.dominio.command;

import br.edu.sgaedu.dominio.entidade.Matricula;
import java.time.LocalDate;
import java.util.function.Consumer;

/**
 * Comando Concreto — encapsula o cancelamento de uma matrícula ativa.
 * Suporta desfazer (reativa a matrícula cancelada).
 */
public class CancelarMatriculaCommand implements Comando {

    private final Matricula matricula;
    private final String motivo;
    private final Consumer<Matricula> atualizarMatricula;

    /** Status anterior — preservado para desfazer(). */
    private Matricula.Status statusAnterior;

    public CancelarMatriculaCommand(Matricula matricula,
                                     String motivo,
                                     Consumer<Matricula> atualizarMatricula) {
        this.matricula = matricula;
        this.motivo = motivo;
        this.atualizarMatricula = atualizarMatricula;
    }

    @Override
    public void executar() {
        if (matricula.getStatus() != Matricula.Status.ATIVA) {
            throw new IllegalStateException("Apenas matrículas ATIVAS podem ser canceladas.");
        }
        statusAnterior = matricula.getStatus();
        matricula.setStatus(Matricula.Status.CANCELADA);
        matricula.setDataCancelamento(LocalDate.now());
        matricula.setMotivoCancelamento(motivo);
        atualizarMatricula.accept(matricula);
    }

    /** Reativa a matrícula (desfaz o cancelamento). */
    @Override
    public void desfazer() {
        if (statusAnterior == null) {
            throw new IllegalStateException("Comando não foi executado — nada a desfazer.");
        }
        matricula.setStatus(statusAnterior);
        matricula.setDataCancelamento(null);
        matricula.setMotivoCancelamento(null);
        atualizarMatricula.accept(matricula);
        statusAnterior = null;
    }
}
