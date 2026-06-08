package br.edu.sgaedu.dominio.observer;

import br.edu.sgaedu.dominio.entidade.Matricula;

/**
 * Evento Concreto — disparado quando uma Matrícula é confirmada (UC-01).
 */
public class MatriculaConfirmada extends EventoAcademico {

    private final Matricula matricula;

    public MatriculaConfirmada(Matricula matricula) {
        super();
        this.matricula = matricula;
        notificarObservadores();
    }

    @Override
    public String getTipo() { return "MATRICULA_CONFIRMADA"; }

    public Matricula getMatricula() { return matricula; }
}
