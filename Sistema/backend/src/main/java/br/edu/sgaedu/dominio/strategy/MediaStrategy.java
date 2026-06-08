package br.edu.sgaedu.dominio.strategy;

import br.edu.sgaedu.dominio.entidade.Nota;
import java.util.List;

/**
 * Strategy GoF — define a interface para cálculo de média.
 * Implementações concretas: MediaAritmetica, MediaPonderada, MediaPorCompetencias.
 * REGRA: sem imports de Spring nesta interface (puro domínio).
 */
public interface MediaStrategy {

    /**
     * Calcula a média final do aluno a partir de suas notas.
     *
     * @param notas lista de notas do aluno na turma (nunca null, pode ser vazia)
     * @return valor da média calculada (0.0 – 10.0)
     */
    double calcular(List<Nota> notas);

    /**
     * Nome descritivo da estratégia — exibido no boletim.
     */
    String getNome();
}
