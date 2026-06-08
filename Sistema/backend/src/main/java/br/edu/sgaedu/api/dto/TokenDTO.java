package br.edu.sgaedu.api.dto;

/** DTO de resposta para autenticação bem-sucedida. */
public record TokenDTO(
        String token,
        long expiracaoMs
) {}
