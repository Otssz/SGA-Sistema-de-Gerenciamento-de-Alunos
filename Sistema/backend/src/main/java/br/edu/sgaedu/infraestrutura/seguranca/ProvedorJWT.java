package br.edu.sgaedu.infraestrutura.seguranca;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Gerador e validador de tokens JWT (ADR-007).
 * Claims obrigatórios: sub (email), role (papel RBAC), iat, exp.
 */
@Component
public class ProvedorJWT {

    @Value("${sgaedu.jwt.chave-secreta}")
    private String chaveSecreta;

    @Value("${sgaedu.jwt.expiracao-ms}")
    private long expiracaoMs;

    /**
     * Gera um JWT assinado com HMAC-SHA256.
     *
     * @param email email do usuário (subject)
     * @param papel papel RBAC (ex.: "PROFESSOR")
     * @return token JWT compacto
     */
    public String gerarToken(String email, String papel) {
        Date agora = new Date();
        Date expiracao = new Date(agora.getTime() + expiracaoMs);

        return Jwts.builder()
                .subject(email)
                .claim("role", papel)
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(getChave())
                .compact();
    }

    /**
     * Extrai e valida os claims do token.
     *
     * @param token JWT a validar
     * @return claims do token
     * @throws io.jsonwebtoken.JwtException se inválido ou expirado
     */
    public Claims validarEExtrairClaims(String token) {
        return Jwts.parser()
                .verifyWith(getChave())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extrairEmail(String token) {
        return validarEExtrairClaims(token).getSubject();
    }

    public String extrairPapel(String token) {
        return validarEExtrairClaims(token).get("role", String.class);
    }

    public boolean isTokenValido(String token) {
        try {
            validarEExtrairClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public long getExpiracaoMs() { return expiracaoMs; }

    private SecretKey getChave() {
        return Keys.hmacShaKeyFor(chaveSecreta.getBytes(StandardCharsets.UTF_8));
    }
}
