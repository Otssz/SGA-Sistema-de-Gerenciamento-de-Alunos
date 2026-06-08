package br.edu.sgaedu.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record AtualizarAlunoDTO(
        @NotBlank @Size(max = 150) String nome,
        @NotBlank @Email @Size(max = 200) String email,
        @NotBlank @Size(max = 20) String matricula,
        @NotNull @Past LocalDate dataNascimento,
        @Size(max = 14) String cpf
) {
}
