package br.edu.sgaedu.dominio.observer;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Collections;
import java.util.List;

/**
 * Observer Concreto — acumula eventos recentes para exibição no painel
 * administrativo da secretaria (sem dependência de framework de notificação externo).
 *
 * Mantém fila FIFO com os últimos {@code CAPACIDADE_MAXIMA} eventos em memória.
 */
public class ObservadorPainel implements ObservadorAcademico {

    private static final int CAPACIDADE_MAXIMA = 100;

    private final Deque<EventoAcademico> eventosRecentes = new ArrayDeque<>();

    @Override
    public void atualizar(EventoAcademico evento) {
        if (eventosRecentes.size() >= CAPACIDADE_MAXIMA) {
            eventosRecentes.pollFirst();
        }
        eventosRecentes.addLast(evento);
    }

    /** Retorna cópia imutável dos eventos recentes (mais novo por último). */
    public List<EventoAcademico> getEventosRecentes() {
        return Collections.unmodifiableList(List.copyOf(eventosRecentes));
    }

    public void limpar() {
        eventosRecentes.clear();
    }
}
