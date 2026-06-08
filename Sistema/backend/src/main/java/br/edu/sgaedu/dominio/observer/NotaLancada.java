package br.edu.sgaedu.dominio.observer;

import br.edu.sgaedu.dominio.entidade.Nota;
import br.edu.sgaedu.dominio.entidade.Professor;

/**
 * Evento Concreto — disparado quando um Professor lança uma Nota (UC-02).
 * Notifica observadores registrados (e-mail ao aluno, painel da secretaria, etc.).
 */
public class NotaLancada extends EventoAcademico {

    private final Nota nota;
    private final Professor professor;

    public NotaLancada(Nota nota, Professor professor) {
        super();
        this.nota = nota;
        this.professor = professor;
        notificarObservadores();
    }

    @Override
    public String getTipo() { return "NOTA_LANCADA"; }

    public Nota getNota() { return nota; }

    public Professor getProfessor() { return professor; }
}
