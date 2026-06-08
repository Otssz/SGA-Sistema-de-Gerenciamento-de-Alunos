package br.edu.sgaedu.aplicacao;

import br.edu.sgaedu.api.dto.LoginDTO;
import br.edu.sgaedu.api.dto.TokenDTO;
import br.edu.sgaedu.dominio.entidade.Usuario;
import br.edu.sgaedu.infraestrutura.persistencia.repositorio.UsuarioRepositorio;
import br.edu.sgaedu.infraestrutura.seguranca.ProvedorJWT;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço de autenticação — valida credenciais e emite JWT (ADR-007).
 * REGRA: senhas nunca trafegam nem são armazenadas em texto puro (LGPD/LGPD-001).
 */
@Service
@Transactional(readOnly = true)
public class AuthService {

    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder passwordEncoder;
    private final ProvedorJWT provedorJWT;
    private final AuditoriaService auditoriaService;

    public AuthService(UsuarioRepositorio usuarioRepositorio,
                       PasswordEncoder passwordEncoder,
                       ProvedorJWT provedorJWT,
                       AuditoriaService auditoriaService) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.passwordEncoder = passwordEncoder;
        this.provedorJWT = provedorJWT;
        this.auditoriaService = auditoriaService;
    }

    /**
     * Autentica o usuário e retorna um JWT.
     *
     * @param dto  credenciais (email + senha)
     * @param ip   IP do cliente para auditoria
     * @return DTO com token JWT e prazo de expiração
     * @throws org.springframework.security.authentication.BadCredentialsException se inválido
     */
    public TokenDTO autenticar(LoginDTO dto, String ip) {
        Usuario usuario = usuarioRepositorio.findByEmail(dto.email())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        if (!usuario.isAtivo()) {
            throw new IllegalStateException("Conta desativada. Contate a secretaria.");
        }

        if (!passwordEncoder.matches(dto.senha(), usuario.getSenhaHash())) {
            // Auditoria de tentativa falha (LGPD/segurança)
            auditoriaService.registrar("LOGIN_FALHO", "Usuario", usuario.getId(), dto.email(), ip);
            throw new org.springframework.security.authentication.BadCredentialsException("Credenciais inválidas.");
        }

        String token = provedorJWT.gerarToken(usuario.getEmail(), usuario.getPapel());

        auditoriaService.registrar("LOGIN_SUCESSO", "Usuario", usuario.getId(), usuario.getEmail(), ip);

        return new TokenDTO(token, provedorJWT.getExpiracaoMs());
    }
}
