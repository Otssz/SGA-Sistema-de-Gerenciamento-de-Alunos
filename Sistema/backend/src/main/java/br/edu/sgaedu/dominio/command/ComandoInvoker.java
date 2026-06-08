package br.edu.sgaedu.dominio.command;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Invoker do Command GoF — mantém o histórico de comandos executados
 * e permite desfazer a última operação.
 *
 * Thread-safety: não garantida — cada requisição HTTP deve usar
 * sua própria instância (escopo de request no Spring).
 */
public class ComandoInvoker {

    private final Deque<Comando> historico = new ArrayDeque<>();

    /**
     * Executa o comando e o empilha para eventual desfazer.
     *
     * @param comando comando a executar
     */
    public void executar(Comando comando) {
        comando.executar();
        historico.push(comando);
    }

    /**
     * Desfaz o último comando executado.
     *
     * @throws IllegalStateException se não houver comando a desfazer
     */
    public void desfazerUltimo() {
        if (historico.isEmpty()) {
            throw new IllegalStateException("Nenhum comando a desfazer.");
        }
        Comando ultimo = historico.pop();
        ultimo.desfazer();
    }

    /** Retorna quantos comandos estão no histórico. */
    public int tamanhoHistorico() {
        return historico.size();
    }
}
