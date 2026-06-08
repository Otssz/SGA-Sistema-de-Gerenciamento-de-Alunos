package br.edu.sgaedu.api.controller;

import br.edu.sgaedu.api.dto.MatricularAlunoDTO;
import br.edu.sgaedu.api.dto.MatriculaResumoDTO;
import br.edu.sgaedu.aplicacao.MatriculaService;
import br.edu.sgaedu.dominio.entidade.Matricula;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Comparator;
import java.util.List;

/**
 * Controller de matrículas (UC-01).
 * RBAC: SECRETARIA e DIRETOR podem matricular/cancelar.
 */
@RestController
@RequestMapping("/matriculas")
public class MatriculaController {

    private final MatriculaService matriculaService;

    public MatriculaController(MatriculaService matriculaService) {
        this.matriculaService = matriculaService;
    }

    /**
     * POST /api/v1/matriculas
     * Matricula um aluno em uma turma (UC-01).
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('SECRETARIA', 'DIRETOR', 'ROLE_SECRETARIA', 'ROLE_DIRETOR')")
    public ResponseEntity<Matricula> matricular(@Valid @RequestBody MatricularAlunoDTO dto,
                                                 @AuthenticationPrincipal String email,
                                                 HttpServletRequest request) {
        Matricula matricula = matriculaService.matricular(dto, email, obterIp(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(matricula);
    }

    /**
     * DELETE /api/v1/matriculas/{id}?motivo=...
     * Cancela uma matrícula ativa.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SECRETARIA', 'DIRETOR', 'ROLE_SECRETARIA', 'ROLE_DIRETOR')")
    public ResponseEntity<Void> cancelar(@PathVariable Long id,
                                          @RequestParam String motivo,
                                          @AuthenticationPrincipal String email,
                                          HttpServletRequest request) {
        matriculaService.cancelar(id, motivo, email, obterIp(request));
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/v1/matriculas?alunoId={alunoId}
     * Lista matrículas de um aluno.
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('SECRETARIA', 'DIRETOR', 'ALUNO', 'ROLE_SECRETARIA', 'ROLE_DIRETOR', 'ROLE_ALUNO')")
    public ResponseEntity<List<Matricula>> listar(@RequestParam Long alunoId) {
        return ResponseEntity.ok(matriculaService.listarPorAluno(alunoId));
    }

    @GetMapping("/todas")
    @PreAuthorize("hasAnyAuthority('SECRETARIA', 'DIRETOR', 'ROLE_SECRETARIA', 'ROLE_DIRETOR')")
    public ResponseEntity<List<MatriculaResumoDTO>> listarTodas() {
        List<MatriculaResumoDTO> matriculas = matriculaService.listarTodas().stream()
                .sorted(Comparator
                        .comparing((Matricula m) -> m.getTurma() == null ? "" : m.getTurma().getCodigo(), String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(m -> m.getAluno() == null ? "" : m.getAluno().getNome(), String.CASE_INSENSITIVE_ORDER))
                .map(this::toResumo)
                .toList();
        return ResponseEntity.ok(matriculas);
    }

    private MatriculaResumoDTO toResumo(Matricula matricula) {
        String aluno = matricula.getAluno() == null ? "-" : matricula.getAluno().getNome();
        String turma = matricula.getTurma() == null ? "-" : matricula.getTurma().getCodigo();
        Long alunoId = matricula.getAluno() == null ? null : matricula.getAluno().getId();
        Long turmaId = matricula.getTurma() == null ? null : matricula.getTurma().getId();
        return new MatriculaResumoDTO(
                matricula.getId(),
                alunoId,
                aluno,
                turmaId,
                turma,
                matricula.getStatus(),
                matricula.getDataEfetivacao()
        );
    }

    private String obterIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        return (forwarded != null && !forwarded.isBlank())
                ? forwarded.split(",")[0].trim()
                : request.getRemoteAddr();
    }
}
