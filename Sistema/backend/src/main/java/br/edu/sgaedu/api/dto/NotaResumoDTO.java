package br.edu.sgaedu.api.dto;

import java.time.LocalDateTime;

public record NotaResumoDTO(
        Long id,
        Long alunoId,
        String aluno,
        Long avaliacaoId,
        String avaliacao,
        double peso,
        double valor,
        String observacao,
        LocalDateTime lancadaEm,
        double mediaFinal,
        String situacao
) {
}
