package br.edu.sgaedu.infraestrutura;

import br.edu.sgaedu.dominio.observer.ObservadorAcademico;
import br.edu.sgaedu.dominio.observer.ObservadorEmail;
import br.edu.sgaedu.dominio.observer.ObservadorPainel;
import br.edu.sgaedu.dominio.observer.ObservadorPush;
import br.edu.sgaedu.dominio.strategy.MediaAritmetica;
import br.edu.sgaedu.dominio.strategy.MediaPonderada;
import br.edu.sgaedu.dominio.strategy.MediaPorCompetencias;
import br.edu.sgaedu.dominio.strategy.MediaStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Map;

/**
 * Registra beans de dominio puro sem acoplar as classes ao Spring.
 */
@Configuration
public class ConfiguracaoBeans {

    @Bean
    public MediaAritmetica mediaAritmetica() {
        return new MediaAritmetica();
    }

    @Bean
    public MediaPonderada mediaPonderada() {
        return new MediaPonderada();
    }

    @Bean
    public MediaPorCompetencias mediaPorCompetencias() {
        return new MediaPorCompetencias();
    }

    @Bean
    public MediaStrategy estrategiaPadrao() {
        return new MediaAritmetica();
    }

    @Bean
    public Map<String, MediaStrategy> estrategiasPorCurso() {
        return Map.of(
                "Ensino Medio 1 Ano", new MediaAritmetica(),
                "Ensino Medio 2 Ano", new MediaPonderada(),
                "Ensino Medio 3 Ano", new MediaPonderada(),
                "Tecnico", new MediaPorCompetencias(),
                "Ensino MÃ©dio 1Âº Ano", new MediaAritmetica(),
                "Ensino MÃ©dio 2Âº Ano", new MediaPonderada(),
                "Ensino MÃ©dio 3Âº Ano", new MediaPonderada(),
                "TÃ©cnico", new MediaPorCompetencias()
        );
    }

    @Bean
    public ObservadorAcademico observadorPainel() {
        return new ObservadorPainel();
    }

    @Bean
    public ObservadorAcademico observadorEmail() {
        return new ObservadorEmail((destinatario, assunto, corpo) -> { });
    }

    @Bean
    public ObservadorAcademico observadorPush() {
        return new ObservadorPush((tokenDispositivo, titulo, mensagem) -> { });
    }
}
