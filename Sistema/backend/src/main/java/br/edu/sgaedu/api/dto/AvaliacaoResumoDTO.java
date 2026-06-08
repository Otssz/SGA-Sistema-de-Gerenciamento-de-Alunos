package br.edu.sgaedu.api.dto;

public record AvaliacaoResumoDTO(Long id, String descricao, Long turmaId, String turma, double peso, double valorMaximo) {
}
