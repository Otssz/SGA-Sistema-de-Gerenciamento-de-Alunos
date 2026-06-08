package br.edu.sgaedu.dominio.strategy;

import br.edu.sgaedu.dominio.entidade.Avaliacao;
import br.edu.sgaedu.dominio.entidade.Nota;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("MediaAritmetica — Strategy Concreto")
class MediaAritmeticaTest {

    private MediaAritmetica strategy;

    @BeforeEach
    void setUp() {
        strategy = new MediaAritmetica();
    }

    @Test
    @DisplayName("deve retornar 0.0 para lista vazia")
    void deveRetornarZeroParaListaVazia() {
        double resultado = strategy.calcular(Collections.emptyList());
        assertThat(resultado).isEqualTo(0.0);
    }

    @Test
    @DisplayName("deve retornar 0.0 para lista nula")
    void deveRetornarZeroParaListaNula() {
        double resultado = strategy.calcular(null);
        assertThat(resultado).isEqualTo(0.0);
    }

    @Test
    @DisplayName("deve calcular a média correta de uma nota")
    void deveCalcularMediaDeUmaNota() {
        List<Nota> notas = List.of(criarNota(8.0));
        double resultado = strategy.calcular(notas);
        assertThat(resultado).isEqualTo(8.0);
    }

    @Test
    @DisplayName("deve calcular a média aritmética de várias notas")
    void deveCalcularMediaAritmeticaDeVariasNotas() {
        List<Nota> notas = List.of(
                criarNota(6.0),
                criarNota(8.0),
                criarNota(10.0)
        );
        double resultado = strategy.calcular(notas);
        assertThat(resultado).isEqualTo(8.0);
    }

    @Test
    @DisplayName("deve calcular média com casas decimais corretamente")
    void deveCalcularMediaComDecimais() {
        List<Nota> notas = List.of(
                criarNota(7.0),
                criarNota(8.0)
        );
        double resultado = strategy.calcular(notas);
        assertThat(resultado).isEqualTo(7.5);
    }

    @Test
    @DisplayName("getNome deve retornar o nome correto")
    void deveRetornarNomeCorreto() {
        assertThat(strategy.getNome()).isEqualTo("Média Aritmética");
    }

    private Nota criarNota(double valor) {
        Nota nota = new Nota();
        nota.setValor(valor);
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setPeso(1.0);
        nota.setAvaliacao(avaliacao);
        return nota;
    }
}
