package br.edu.sgaedu.api.dto;

public record FrequenciaResumoDTO(
    long totalAulas,
    long presencas,
    long faltas,
    double percentualFrequencia,
    String situacao
) {}
