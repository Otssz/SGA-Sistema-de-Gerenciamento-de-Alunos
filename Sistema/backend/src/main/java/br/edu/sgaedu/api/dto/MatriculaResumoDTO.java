package br.edu.sgaedu.api.dto;

import br.edu.sgaedu.dominio.entidade.Matricula;
import java.time.LocalDate;

public record MatriculaResumoDTO(
        Long id,
        Long alunoId,
        String aluno,
        Long turmaId,
        String turma,
        Matricula.Status status,
        LocalDate dataEfetivacao
) {
}
