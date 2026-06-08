package br.edu.sgaedu.dominio.observer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Subject abstrato do Observer (ADR-005).
 * Subclasses: NotaLancada, MatriculaConfirmada, RiscoReprovacao.
 * REGRA: sem imports de Spring nesta classe (puro domínio).
 */
public abstract class EventoAcademico {

    private final LocalDateTime ocorridoEm;
    private final List<ObservadorAcademico> observadores = new ArrayList<>();

    protected EventoAcademico() {
        this.ocorridoEm = LocalDateTime.now();
    }

    /** Registra um observador para este evento. */
    public void adicionarObservador(ObservadorAcademico observador) {
        observadores.add(observador);
    }

    /** Remove um observador. */
    public void removerObservador(ObservadorAcademico observador) {
        observadores.remove(observador);
    }

    /** Notifica todos os observadores registrados. */
    protected void notificarObservadores() {
        for (ObservadorAcademico obs : observadores) {
            obs.atualizar(this);
        }
    }

    /** Tipo do evento — usado para roteamento de notificações. */
    public abstract String getTipo();

    public LocalDateTime getOcorridoEm() { return ocorridoEm; }
}
