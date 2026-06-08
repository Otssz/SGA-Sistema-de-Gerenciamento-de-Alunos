package br.edu.sgaedu.dominio.strategy;

import br.edu.sgaedu.dominio.entidade.Nota;
import java.util.List;

/**
 * Strategy Concreto — média ponderada pelo campo {@code Avaliacao.peso}.
 * Fórmula: Σ(valor × peso) / Σ(peso).
 */
public class MediaPonderada implements MediaStrategy {

    @Override
    public double calcular(List<Nota> notas) {
        if (notas == null || notas.isEmpty()) {
            return 0.0;
        }

        double somaPonderada = 0.0;
        double somaPesos = 0.0;

        for (Nota nota : notas) {
            double peso = nota.getAvaliacao().getPeso();
            somaPonderada += nota.getValor() * peso;
            somaPesos += peso;
        }

        if (somaPesos == 0.0) {
            return 0.0;
        }
        return somaPonderada / somaPesos;
    }

    @Override
    public String getNome() {
        return "Média Ponderada";
    }
}
