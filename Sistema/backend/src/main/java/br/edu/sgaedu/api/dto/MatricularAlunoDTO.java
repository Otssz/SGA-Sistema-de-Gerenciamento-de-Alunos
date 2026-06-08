package br.edu.sgaedu.api.dto;

import jakarta.validation.constraints.NotNull;

/** DTO de entrada para matrícula de aluno (UC-01). */
public record MatricularAlunoDTO(
        @NotNull
        Long alunoId,

        @NotNull
        Long turmaId
) {}
