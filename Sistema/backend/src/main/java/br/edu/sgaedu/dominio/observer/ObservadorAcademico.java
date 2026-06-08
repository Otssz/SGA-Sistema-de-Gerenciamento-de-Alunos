package br.edu.sgaedu.dominio.observer;

/**
 * Observer abstrato do padrão Observer (ADR-005).
 * Implementações: ObservadorEmail, ObservadorPush, ObservadorPainel.
 * REGRA: sem imports de Spring nesta interface (puro domínio).
 */
public interface ObservadorAcademico {

    /**
     * Chamado pelo Subject quando um evento acadêmico ocorre.
     *
     * @param evento evento disparado (NotaLancada, MatriculaConfirmada, RiscoReprovacao, etc.)
     */
    void atualizar(EventoAcademico evento);
}
