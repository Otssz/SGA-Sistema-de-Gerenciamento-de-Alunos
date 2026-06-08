package br.edu.sgaedu.api.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record RegistrarFrequenciaDTO(
    @NotNull Long alunoId,
    @NotNull LocalDate data,
    @NotNull Boolean presente,
    String justificativa
) {}
