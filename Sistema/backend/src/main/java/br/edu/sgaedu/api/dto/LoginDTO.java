package br.edu.sgaedu.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** DTO de entrada para autenticação. */
public record LoginDTO(
        @NotBlank @Email
        String email,

        @NotBlank @Size(min = 8, max = 72)
        String senha
) {}
