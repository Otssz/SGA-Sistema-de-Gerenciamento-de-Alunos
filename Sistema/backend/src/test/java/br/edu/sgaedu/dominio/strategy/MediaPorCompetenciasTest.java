package br.edu.sgaedu.dominio.strategy;

import br.edu.sgaedu.dominio.entidade.Avaliacao;
import br.edu.sgaedu.dominio.entidade.Nota;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("MediaPorCompetencias — Strategy Concreto")
class MediaPorCompetenciasTest {

    private MediaPorCompetencias strategy;

    @BeforeEach
    void setUp() {
        strategy = new MediaPorCompetencias();
    }

    @Test
    @DisplayName("deve retornar 0.0 para lista vazia")
    void deveRetornarZeroParaListaVazia() {
        assertThat(strategy.calcular(Collections.emptyList())).isEqualTo(0.0);
    }

    @Test
    @DisplayName("deve calcular média de uma única competência")
    void deveCalcularMediaDeUmaCompetencia() {
        List<Nota> notas = List.of(
                criarNotaComCompetencia(8.0, "C1"),
                criarNotaComCompetencia(6.0, "C1")
        );
        // Competência C1: (8 + 6) / 2 = 7.0 → média total = 7.0
        assertThat(strategy.calcular(notas)).isEqualTo(7.0);
    }

    @Test
    @DisplayName("deve calcular média de múltiplas competências")
    void deveCalcularMediaDeMultiplasCompetencias() {
        List<Nota> notas = List.of(
                criarNotaComCompetencia(8.0, "C1"),  // C1: 8.0
                criarNotaComCompetencia(6.0, "C2"),  // C2: 6.0
                criarNotaComCompetencia(10.0, "C3")  // C3: 10.0
        );
        // Média das competências: (8 + 6 + 10) / 3 = 8.0
        assertThat(strategy.calcular(notas)).isEqualTo(8.0);
    }

    @Test
    @DisplayName("deve ponderar competência com múltiplas notas corretamente")
    void devePonderarCompetenciaComMultiplasNotas() {
        List<Nota> notas = List.of(
                criarNotaComCompetencia(7.0, "C1"),
                criarNotaComCompetencia(9.0, "C1"),  // C1: (7+9)/2 = 8.0
                criarNotaComCompetencia(5.0, "C2")   // C2: 5.0
        );
        // Média: (8.0 + 5.0) / 2 = 6.5
        assertThat(strategy.calcular(notas)).isCloseTo(6.5, within(0.001));
    }

    private Nota criarNotaComCompetencia(double valor, String codigoCompetencia) {
        Nota nota = new Nota();
        nota.setValor(valor);
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setDescricao(codigoCompetencia);
        avaliacao.setPeso(1.0);
        nota.setAvaliacao(avaliacao);
        return nota;
    }
}
