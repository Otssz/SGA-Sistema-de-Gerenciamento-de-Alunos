package br.edu.sgaedu.dominio.observer;

import br.edu.sgaedu.dominio.entidade.Aluno;
import br.edu.sgaedu.dominio.entidade.Turma;

/**
 * Evento Concreto — disparado pelo MediaService quando a média do aluno
 * fica abaixo do limiar de reprovação (UC-03).
 * Notifica responsáveis e coordenação.
 */
public class RiscoReprovacao extends EventoAcademico {

    private final Aluno aluno;
    private final Turma turma;
    private final double mediaAtual;
    private final double limiarReprovacao;

    public RiscoReprovacao(Aluno aluno, Turma turma, double mediaAtual, double limiarReprovacao) {
        super();
        this.aluno = aluno;
        this.turma = turma;
        this.mediaAtual = mediaAtual;
        this.limiarReprovacao = limiarReprovacao;
        notificarObservadores();
    }

    @Override
    public String getTipo() { return "RISCO_REPROVACAO"; }

    public Aluno getAluno() { return aluno; }

    public Turma getTurma() { return turma; }

    public double getMediaAtual() { return mediaAtual; }

    public double getLimiarReprovacao() { return limiarReprovacao; }
}
