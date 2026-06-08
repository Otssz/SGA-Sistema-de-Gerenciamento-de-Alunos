package br.edu.sgaedu.api.dto;

import java.time.LocalDate;

public record AlunoDetalheDTO(
        Long id,
        String nome,
        String email,
        String matricula,
        LocalDate dataNascimento,
        String cpf
) {
}
