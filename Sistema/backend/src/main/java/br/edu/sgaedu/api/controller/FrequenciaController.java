package br.edu.sgaedu.api.controller;

import br.edu.sgaedu.api.dto.FrequenciaResumoDTO;
import br.edu.sgaedu.api.dto.RegistrarFrequenciaDTO;
import br.edu.sgaedu.aplicacao.FrequenciaService;
import br.edu.sgaedu.dominio.entidade.Frequencia;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controller de frequência (UC-05).
 * RBAC: somente PROFESSOR registra; todos consultam o resumo.
 */
@RestController
@RequestMapping("/turmas/{turmaId}/frequencias")
public class FrequenciaController {

    private final FrequenciaService frequenciaService;

    public FrequenciaController(FrequenciaService frequenciaService) {
        this.frequenciaService = frequenciaService;
    }

    /** POST /api/v1/turmas/{turmaId}/frequencias — registra presença/falta (UC-05). */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('PROFESSOR', 'ROLE_PROFESSOR')")
    public ResponseEntity<Frequencia> registrar(@PathVariable Long turmaId,
                                                @Valid @RequestBody RegistrarFrequenciaDTO dto,
                                                @AuthenticationPrincipal String emailProfessor,
                                                HttpServletRequest request) {
        Frequencia f = frequenciaService.registrar(turmaId, dto,
                emailProfessor, obterIp(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(f);
    }

    /** GET /api/v1/turmas/{turmaId}/frequencias/resumo?alunoId={id} — resumo com RN-001. */
    @GetMapping("/resumo")
    @PreAuthorize("hasAnyAuthority('PROFESSOR', 'ALUNO', 'SECRETARIA', 'DIRETOR', 'ROLE_PROFESSOR', 'ROLE_ALUNO', 'ROLE_SECRETARIA', 'ROLE_DIRETOR')")
    public ResponseEntity<FrequenciaResumoDTO> resumo(@PathVariable Long turmaId,
                                                      @RequestParam Long alunoId) {
        return ResponseEntity.ok(frequenciaService.resumo(turmaId, alunoId));
    }

    private String obterIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        return (forwarded != null && !forwarded.isBlank())
                ? forwarded.split(",")[0].trim()
                : request.getRemoteAddr();
    }
}
