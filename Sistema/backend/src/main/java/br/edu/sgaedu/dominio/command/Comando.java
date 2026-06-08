package br.edu.sgaedu.dominio.command;

/**
 * Interface Command do padrão Command GoF (ADR-005).
 * Encapsula uma operação como objeto — suporta desfazer (undo).
 * REGRA: sem imports de Spring nesta interface (puro domínio).
 */
public interface Comando {

    /** Executa a operação encapsulada. */
    void executar();

    /**
     * Desfaz a operação executada.
     * Lança {@link UnsupportedOperationException} se o comando não for reversível.
     */
    void desfazer();
}
