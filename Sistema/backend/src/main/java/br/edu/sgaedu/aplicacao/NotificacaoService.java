package br.edu.sgaedu.aplicacao;

import br.edu.sgaedu.dominio.observer.EventoAcademico;
import br.edu.sgaedu.dominio.observer.ObservadorAcademico;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Broker de eventos — registra observadores e publica eventos acadêmicos.
 * Atua como intermediário entre o domínio (Observer) e a infraestrutura
 * (JavaMailSender, FCM/push), mantendo o domínio desacoplado.
 *
 * Em produção, substitua por Spring ApplicationEventPublisher para
 * suporte a eventos assíncronos.
 */
@Service
public class NotificacaoService {

    private final List<ObservadorAcademico> observadores;

    /** Todos os beans que implementam ObservadorAcademico são injetados automaticamente. */
    public NotificacaoService(List<ObservadorAcademico> observadores) {
        this.observadores = observadores;
    }

    /**
     * Registra todos os observadores no evento e o dispara.
     *
     * @param evento evento acadêmico a publicar
     */
    public void publicar(EventoAcademico evento) {
        observadores.forEach(obs -> obs.atualizar(evento));
    }
}
