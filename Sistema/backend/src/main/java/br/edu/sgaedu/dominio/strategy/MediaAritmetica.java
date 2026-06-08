package br.edu.sgaedu.dominio.strategy;

import br.edu.sgaedu.dominio.entidade.Nota;
import java.util.List;

/**
 * Strategy Concreto — média simples: soma / quantidade.
 * Usada em cursos sem diferenciação de peso entre avaliações.
 */
public class MediaAritmetica implements MediaStrategy {

    @Override
    public double calcular(List<Nota> notas) {
        if (notas == null || notas.isEmpty()) {
            return 0.0;
        }
        double soma = notas.stream()
                .mapToDouble(Nota::getValor)
                .sum();
        return soma / notas.size();
    }

    @Override
    public String getNome() {
        return "Média Aritmética";
    }
}
