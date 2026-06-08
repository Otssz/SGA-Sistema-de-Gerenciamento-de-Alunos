package br.edu.sgaedu.dominio.strategy;

import br.edu.sgaedu.dominio.entidade.Avaliacao;
import br.edu.sgaedu.dominio.entidade.Nota;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("MediaPonderada — Strategy Concreto")
class MediaPonderadaTest {

    private MediaPonderada strategy;

    @BeforeEach
    void setUp() {
        strategy = new MediaPonderada();
    }

    @Test
    @DisplayName("deve retornar 0.0 para lista vazia")
    void deveRetornarZeroParaListaVazia() {
        assertThat(strategy.calcular(Collections.emptyList())).isEqualTo(0.0);
    }

    @Test
    @DisplayName("deve retornar 0.0 quando soma dos pesos é zero")
    void deveRetornarZeroQuandoPesosZerados() {
        List<Nota> notas = List.of(criarNotaComPeso(8.0, 0.0));
        assertThat(strategy.calcular(notas)).isEqualTo(0.0);
    }

    @Test
    @DisplayName("deve calcular média ponderada com dois pesos diferentes")
    void deveCalcularMediaPonderadaComPesosDiferentes() {
        // (7 * 2 + 9 * 3) / (2 + 3) = (14 + 27) / 5 = 8.2
        List<Nota> notas = List.of(
                criarNotaComPeso(7.0, 2.0),
                criarNotaComPeso(9.0, 3.0)
        );
        double resultado = strategy.calcular(notas);
        assertThat(resultado).isCloseTo(8.2, within(0.001));
    }

    @Test
    @DisplayName("com pesos iguais deve se comportar como média aritmética")
    void comPesosIguaisDeveFuncionarComoAritmetica() {
        List<Nota> notas = List.of(
                criarNotaComPeso(6.0, 1.0),
                criarNotaComPeso(8.0, 1.0)
        );
        assertThat(strategy.calcular(notas)).isEqualTo(7.0);
    }

    @Test
    @DisplayName("deve lidar com nota única corretamente")
    void deveCalcularNotaUnica() {
        List<Nota> notas = List.of(criarNotaComPeso(9.5, 4.0));
        assertThat(strategy.calcular(notas)).isEqualTo(9.5);
    }

    private Nota criarNotaComPeso(double valor, double peso) {
        Nota nota = new Nota();
        nota.setValor(valor);
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setPeso(peso);
        nota.setAvaliacao(avaliacao);
        return nota;
    }
}
