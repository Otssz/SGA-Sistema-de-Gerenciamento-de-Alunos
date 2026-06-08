package br.edu.sgaedu.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record CadastrarAvaliacaoDTO(
        @NotBlank @Size(max = 100) String descricao,
        @NotNull LocalDate dataAplicacao,
        @Positive double peso,
        @DecimalMin("0.1") double valorMaximo
) {
}
