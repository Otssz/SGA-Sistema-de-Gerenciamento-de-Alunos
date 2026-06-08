package br.edu.sgaedu.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CadastrarTurmaDTO(
        @NotBlank @Size(max = 20) String codigo,
        @NotBlank @Size(max = 10) String anoLetivo,
        @NotBlank @Size(max = 20) String turno,
        @NotBlank @Size(max = 100) String curso,
        @Positive int cargaHorariaTotal
) {
}
