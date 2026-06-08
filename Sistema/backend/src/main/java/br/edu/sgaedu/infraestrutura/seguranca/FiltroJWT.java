package br.edu.sgaedu.infraestrutura.seguranca;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

/**
 * Filtro HTTP — extrai e valida o JWT de cada requisição.
 * Executa uma vez por request (OncePerRequestFilter).
 * Popula o SecurityContext com o usuário autenticado e seu papel RBAC.
 */
@Component
public class FiltroJWT extends OncePerRequestFilter {

    private final ProvedorJWT provedorJWT;

    public FiltroJWT(ProvedorJWT provedorJWT) {
        this.provedorJWT = provedorJWT;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String cabecalho = request.getHeader("Authorization");

        if (cabecalho == null || !cabecalho.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = cabecalho.substring(7);

        if (!provedorJWT.isTokenValido(token)) {
            chain.doFilter(request, response);
            return;
        }

        String email = provedorJWT.extrairEmail(token);
        String papel = provedorJWT.extrairPapel(token);
        String papelSemPrefixo = papel != null && papel.startsWith("ROLE_")
                ? papel.substring("ROLE_".length())
                : papel;

        var autenticacao = new UsernamePasswordAuthenticationToken(
                email, null,
                List.of(
                        new SimpleGrantedAuthority("ROLE_" + papelSemPrefixo),
                        new SimpleGrantedAuthority(papelSemPrefixo)
                )
        );
        autenticacao.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(autenticacao);
        chain.doFilter(request, response);
    }
}
