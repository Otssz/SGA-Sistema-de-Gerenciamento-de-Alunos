package br.edu.sgaedu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Ponto de entrada do SGA-Edu.
 * @EnableScheduling habilita o agendador de médias (UC-03).
 */
@SpringBootApplication
@EnableScheduling
public class SgaEduApplication {

    public static void main(String[] args) {
        SpringApplication.run(SgaEduApplication.class, args);
    }
}
