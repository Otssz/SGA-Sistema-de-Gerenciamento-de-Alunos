package br.edu.sgaedu.api.controller;

import br.edu.sgaedu.api.dto.LancarNotaDTO;
import br.edu.sgaedu.api.dto.NotaResumoDTO;
import br.edu.sgaedu.aplicacao.NotaService;
import br.edu.sgaedu.dominio.entidade.Nota;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller de notas.
 * RBAC: somente PROFESSOR pode lançar notas; ALUNO pode consultar as suas.
 * REGRA: não contém lógica de negócio — delega ao NotaService.
 */
@RestController
@RequestMapping("/turmas/{turmaId}/notas")
public class NotaController {

    private final NotaService notaService;

    public NotaController(NotaService notaService) {
        this.notaService = notaService;
    }

    /**
     * POST /api/v1/turmas/{turmaId}/notas
     * Lança uma nota para um aluno (UC-02).
     *
     * @param turmaId  ID da turma (path variable — validado no serviço)
     * @param dto      dados da nota
     * @param usuario  usuário autenticado (extraído do JWT)
     * @param request  para auditoria de IP
     * @return 201 Created com a nota registrada
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('PROFESSOR', 'ROLE_PROFESSOR')")
    public ResponseEntity<Nota> lancarNota(@PathVariable Long turmaId,
                                            @Valid @RequestBody LancarNotaDTO dto,
                                            @AuthenticationPrincipal String emailProfessor,
                                            HttpServletRequest request) {
        Nota nota = notaService.lancarNota(turmaId, dto, emailProfessor, obterIp(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(nota);
    }

    /**
     * GET /api/v1/turmas/{turmaId}/notas?alunoId={alunoId}
     * Lista notas de um aluno em uma turma.
     * ALUNO só pode ver as suas próprias — verificado no NotaService.
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('PROFESSOR', 'ALUNO', 'SECRETARIA', 'DIRETOR', 'ROLE_PROFESSOR', 'ROLE_ALUNO', 'ROLE_SECRETARIA', 'ROLE_DIRETOR')")
    public ResponseEntity<List<NotaResumoDTO>> listarNotas(@PathVariable Long turmaId,
                                                            @RequestParam(required = false) Long alunoId,
                                                            @AuthenticationPrincipal String emailUsuario) {
        List<Nota> notasEncontradas = alunoId == null
                ? notaService.buscarNotasPorTurma(turmaId)
                : notaService.buscarNotasPorAlunoETurma(alunoId, turmaId);
        Map<Long, Double> mediasPorAluno = notasEncontradas.stream()
                .collect(Collectors.groupingBy(
                        nota -> nota.getAluno().getId(),
                        Collectors.collectingAndThen(Collectors.toList(), this::calcularMediaPonderada)
                ));
        List<NotaResumoDTO> notas = notasEncontradas.stream()
                .map(nota -> toResumo(nota, mediasPorAluno.getOrDefault(nota.getAluno().getId(), 0.0)))
                .toList();
        return ResponseEntity.ok(notas);
    }

    private NotaResumoDTO toResumo(Nota nota, double mediaFinal) {
        Long alunoId = nota.getAluno() == null ? null : nota.getAluno().getId();
        String aluno = nota.getAluno() == null ? "-" : nota.getAluno().getNome();
        Long avaliacaoId = nota.getAvaliacao() == null ? null : nota.getAvaliacao().getId();
        String avaliacao = nota.getAvaliacao() == null ? "-" : nota.getAvaliacao().getDescricao();
        double peso = nota.getAvaliacao() == null ? 0.0 : nota.getAvaliacao().getPeso();
        return new NotaResumoDTO(
                nota.getId(),
                alunoId,
                aluno,
                avaliacaoId,
                avaliacao,
                peso,
                nota.getValor(),
                nota.getObservacao(),
                nota.getLancadaEm(),
                mediaFinal,
                situacao(mediaFinal)
        );
    }

    private double calcularMediaPonderada(List<Nota> notas) {
        double somaPesos = notas.stream()
                .mapToDouble(nota -> nota.getAvaliacao() == null ? 0.0 : nota.getAvaliacao().getPeso())
                .sum();
        if (somaPesos == 0.0) {
            return 0.0;
        }
        double soma = notas.stream()
                .mapToDouble(nota -> nota.getValor() * nota.getAvaliacao().getPeso())
                .sum();
        return Math.round((soma / somaPesos) * 100.0) / 100.0;
    }

    private String situacao(double media) {
        if (media >= 6.0) {
            return "APROVADO";
        }
        if (media >= 5.0) {
            return "RECUPERACAO";
        }
        return "REPROVADO";
    }

    private String obterIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        return (forwarded != null && !forwarded.isBlank())
                ? forwarded.split(",")[0].trim()
                : request.getRemoteAddr();
    }
}
