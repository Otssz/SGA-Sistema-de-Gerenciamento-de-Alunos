package br.edu.sgaedu.dominio.observer;

/**
 * Observer concreto para notificacao por e-mail.
 * A infraestrutura real fica fora do dominio e entra por callback.
 */
public class ObservadorEmail implements ObservadorAcademico {

    private final EnviadorEmail enviador;

    public ObservadorEmail(EnviadorEmail enviador) {
        this.enviador = enviador;
    }

    @Override
    public void atualizar(EventoAcademico evento) {
        switch (evento.getTipo()) {
            case "NOTA_LANCADA" -> processarNotaLancada((NotaLancada) evento);
            case "MATRICULA_CONFIRMADA" -> processarMatriculaConfirmada((MatriculaConfirmada) evento);
            case "RISCO_REPROVACAO" -> processarRiscoReprovacao((RiscoReprovacao) evento);
            default -> { /* evento nao tratado por este observador */ }
        }
    }

    private void processarNotaLancada(NotaLancada evento) {
        var nota = evento.getNota();
        String avaliacao = nota.getAvaliacao() == null ? "avaliacao" : nota.getAvaliacao().getDescricao();
        enviador.enviar(nota.getAluno().getEmail(), "Nota lancada",
                "Sua nota em " + avaliacao + " foi registrada: " + nota.getValor() + ".");
    }

    private void processarMatriculaConfirmada(MatriculaConfirmada evento) {
        var matricula = evento.getMatricula();
        String curso = matricula.getTurma().getCurso() == null ? "-" : matricula.getTurma().getCurso().getNome();
        enviador.enviar(matricula.getAluno().getEmail(), "Matricula confirmada",
                "Sua matricula na turma " + matricula.getTurma().getCodigo()
                        + " (" + curso + ") foi confirmada.");
    }

    private void processarRiscoReprovacao(RiscoReprovacao evento) {
        enviador.enviar(evento.getAluno().getEmail(), "Risco academico identificado",
                "Media atual: " + evento.getMediaAtual()
                        + ". Limiar minimo: " + evento.getLimiarReprovacao()
                        + ". Procure a coordenacao para acompanhamento.");
    }

    @FunctionalInterface
    public interface EnviadorEmail {
        void enviar(String destinatario, String assunto, String corpo);
    }
}
