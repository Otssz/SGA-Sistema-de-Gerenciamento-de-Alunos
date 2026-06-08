package br.edu.sgaedu.dominio.strategy;

import br.edu.sgaedu.dominio.entidade.Nota;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Strategy Concreto — cada nota representa uma competência avaliada.
 * Calcula a média de cada competência separadamente e depois tira
 * a média das competências (modelo ENEM / competências socioemocionais).
 *
 * Convenção: {@code Avaliacao.descricao} contém o código da competência
 * (ex.: "C1", "C2", "C3") para agrupamento.
 */
public class MediaPorCompetencias implements MediaStrategy {

    @Override
    public double calcular(List<Nota> notas) {
        if (notas == null || notas.isEmpty()) {
            return 0.0;
        }

        // Agrupa notas por competência (código extraído da descrição da avaliação)
        Map<String, List<Nota>> porCompetencia = notas.stream()
                .collect(Collectors.groupingBy(n -> n.getAvaliacao().getDescricao()));

        // Média por competência
        double somaMediasCompetencias = porCompetencia.values().stream()
                .mapToDouble(grupo -> grupo.stream()
                        .mapToDouble(Nota::getValor)
                        .average()
                        .orElse(0.0))
                .sum();

        return somaMediasCompetencias / porCompetencia.size();
    }

    @Override
    public String getNome() {
        return "Média por Competências";
    }
}
