package br.edu.sgaedu.api.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** DTO de entrada para lançamento de nota (UC-02). */
public record LancarNotaDTO(
        @NotNull
        Long alunoId,

        @NotNull
        Long avaliacaoId,

        @NotNull
        @DecimalMin("0.0") @DecimalMax("10.0")
        Double valor,

        @Size(max = 500)
        String observacao
) {}
