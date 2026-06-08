package br.edu.sgaedu.dominio.observer;

/**
 * Observer concreto para notificacao push.
 * A integracao real com o provedor mobile fica fora do dominio.
 */
public class ObservadorPush implements ObservadorAcademico {

    @FunctionalInterface
    public interface EnviadorPush {
        void enviar(String tokenDispositivo, String titulo, String mensagem);
    }

    private final EnviadorPush enviador;

    public ObservadorPush(EnviadorPush enviador) {
        this.enviador = enviador;
    }

    @Override
    public void atualizar(EventoAcademico evento) {
        switch (evento.getTipo()) {
            case "NOTA_LANCADA" -> {
                NotaLancada notaLancada = (NotaLancada) evento;
                enviador.enviar(tokenAluno(notaLancada.getNota().getAluno().getId()),
                        "Nota lancada",
                        "Nova nota registrada: " + notaLancada.getNota().getValor());
            }
            case "MATRICULA_CONFIRMADA" -> {
                MatriculaConfirmada matriculaConfirmada = (MatriculaConfirmada) evento;
                enviador.enviar(tokenAluno(matriculaConfirmada.getMatricula().getAluno().getId()),
                        "Matricula confirmada",
                        "Turma " + matriculaConfirmada.getMatricula().getTurma().getCodigo());
            }
            case "RISCO_REPROVACAO" -> {
                RiscoReprovacao risco = (RiscoReprovacao) evento;
                enviador.enviar(tokenAluno(risco.getAluno().getId()),
                        "Acompanhamento academico",
                        "Media atual abaixo do limiar: " + risco.getMediaAtual());
            }
            default -> { /* evento nao tratado por este observador */ }
        }
    }

    private String tokenAluno(Long alunoId) {
        return alunoId == null ? "aluno-desconhecido" : "aluno-" + alunoId;
    }
}
