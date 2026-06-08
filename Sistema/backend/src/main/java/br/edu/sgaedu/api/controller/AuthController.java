package br.edu.sgaedu.api.controller;

import br.edu.sgaedu.api.dto.LoginDTO;
import br.edu.sgaedu.api.dto.TokenDTO;
import br.edu.sgaedu.aplicacao.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller de autenticação.
 * REGRA: não contém lógica de negócio — delega tudo ao AuthService.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * POST /api/v1/auth/login
     * Autentica o usuário e retorna um JWT.
     *
     * @param dto     credenciais (email + senha)
     * @param request usado para extrair o IP real do cliente
     * @return 200 com TokenDTO ou 401 se credenciais inválidas
     */
    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody LoginDTO dto,
                                           HttpServletRequest request) {
        String ip = obterIpReal(request);
        TokenDTO token = authService.autenticar(dto, ip);
        return ResponseEntity.ok(token);
    }

    /** Extrai o IP real considerando proxies reversos (X-Forwarded-For). */
    private String obterIpReal(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
