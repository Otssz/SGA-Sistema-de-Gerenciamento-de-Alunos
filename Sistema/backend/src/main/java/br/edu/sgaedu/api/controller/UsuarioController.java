package br.edu.sgaedu.api.controller;

import br.edu.sgaedu.api.dto.AlunoResumoDTO;
import br.edu.sgaedu.api.dto.AlunoDetalheDTO;
import br.edu.sgaedu.api.dto.AtualizarAlunoDTO;
import br.edu.sgaedu.api.dto.CadastrarAlunoDTO;
import br.edu.sgaedu.dominio.entidade.Aluno;
import br.edu.sgaedu.dominio.entidade.Usuario;
import br.edu.sgaedu.infraestrutura.persistencia.repositorio.AlunoRepositorio;
import br.edu.sgaedu.infraestrutura.persistencia.repositorio.UsuarioRepositorio;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

/** Endpoints de dados do usuário logado. */
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private static final String SENHA_INICIAL_ALUNO = "Senha@123";

    private final UsuarioRepositorio usuarioRepositorio;
    private final AlunoRepositorio alunoRepositorio;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioRepositorio usuarioRepositorio,
                             AlunoRepositorio alunoRepositorio,
                             PasswordEncoder passwordEncoder) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.alunoRepositorio = alunoRepositorio;
        this.passwordEncoder = passwordEncoder;
    }

    /** GET /api/v1/usuarios/me — retorna id, nome, email e papel do usuário autenticado. */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> me(@AuthenticationPrincipal String email) {
        Usuario usuario = usuarioRepositorio.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        return ResponseEntity.ok(Map.of(
                "id",    usuario.getId(),
                "nome",  usuario.getNome(),
                "email", usuario.getEmail(),
                "papel", usuario.getPapel()
        ));
    }

    @GetMapping("/alunos")
    @PreAuthorize("hasAnyAuthority('SECRETARIA', 'DIRETOR', 'PROFESSOR', 'ROLE_SECRETARIA', 'ROLE_DIRETOR', 'ROLE_PROFESSOR')")
    public ResponseEntity<List<AlunoResumoDTO>> listarAlunos() {
        List<AlunoResumoDTO> alunos = alunoRepositorio.findAll().stream()
                .map(this::toResumo)
                .toList();
        return ResponseEntity.ok(alunos);
    }

    @GetMapping("/alunos/{id}")
    @PreAuthorize("hasAnyAuthority('SECRETARIA', 'DIRETOR', 'ROLE_SECRETARIA', 'ROLE_DIRETOR')")
    public ResponseEntity<AlunoDetalheDTO> obterAluno(@PathVariable Long id) {
        Aluno aluno = alunoRepositorio.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aluno nao encontrado: " + id));
        return ResponseEntity.ok(toDetalhe(aluno));
    }

    @PostMapping("/alunos")
    @PreAuthorize("hasAnyAuthority('SECRETARIA', 'DIRETOR', 'ROLE_SECRETARIA', 'ROLE_DIRETOR')")
    public ResponseEntity<AlunoResumoDTO> cadastrarAluno(@Valid @RequestBody CadastrarAlunoDTO dto) {
        if (usuarioRepositorio.existsByEmail(dto.email())) {
            throw new IllegalStateException("Ja existe usuario com este e-mail.");
        }
        if (alunoRepositorio.existsByMatricula(dto.matricula())) {
            throw new IllegalStateException("Ja existe aluno com esta matricula.");
        }

        Aluno aluno = new Aluno();
        aluno.setNome(dto.nome());
        aluno.setEmail(dto.email());
        aluno.setSenhaHash(passwordEncoder.encode(SENHA_INICIAL_ALUNO));
        aluno.setMatricula(dto.matricula());
        aluno.setDataNascimento(dto.dataNascimento());
        aluno.setCpf(dto.cpf());

        Aluno salvo = alunoRepositorio.save(aluno);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResumo(salvo));
    }

    @PutMapping("/alunos/{id}")
    @PreAuthorize("hasAnyAuthority('SECRETARIA', 'DIRETOR', 'ROLE_SECRETARIA', 'ROLE_DIRETOR')")
    public ResponseEntity<AlunoDetalheDTO> atualizarAluno(@PathVariable Long id,
                                                           @Valid @RequestBody AtualizarAlunoDTO dto) {
        Aluno aluno = alunoRepositorio.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aluno nao encontrado: " + id));

        usuarioRepositorio.findByEmail(dto.email())
                .filter(usuario -> !usuario.getId().equals(id))
                .ifPresent(usuario -> {
                    throw new IllegalStateException("Ja existe usuario com este e-mail.");
                });

        alunoRepositorio.findByMatricula(dto.matricula())
                .filter(outro -> !outro.getId().equals(id))
                .ifPresent(outro -> {
                    throw new IllegalStateException("Ja existe aluno com esta matricula.");
                });

        aluno.setNome(dto.nome());
        aluno.setEmail(dto.email());
        aluno.setMatricula(dto.matricula());
        aluno.setDataNascimento(dto.dataNascimento());
        aluno.setCpf(dto.cpf());

        return ResponseEntity.ok(toDetalhe(alunoRepositorio.save(aluno)));
    }

    private AlunoResumoDTO toResumo(Aluno aluno) {
        return new AlunoResumoDTO(aluno.getId(), aluno.getNome(), aluno.getEmail(), aluno.getMatricula());
    }

    private AlunoDetalheDTO toDetalhe(Aluno aluno) {
        return new AlunoDetalheDTO(
                aluno.getId(),
                aluno.getNome(),
                aluno.getEmail(),
                aluno.getMatricula(),
                aluno.getDataNascimento(),
                aluno.getCpf()
        );
    }
}
