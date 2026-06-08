package br.edu.sgaedu.api.controller;

import br.edu.sgaedu.aplicacao.DocumentoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

/**
 * Controller de documentos PDF (UC-04).
 * RBAC: SECRETARIA e DIRETOR podem gerar qualquer documento;
 *       ALUNO pode gerar apenas os seus próprios.
 */
@RestController
@RequestMapping("/documentos")
public class DocumentoController {

    private final DocumentoService documentoService;

    public DocumentoController(DocumentoService documentoService) {
        this.documentoService = documentoService;
    }

    /**
     * GET /api/v1/documentos/boletim?alunoId={id}&turmaId={id}&periodo={periodo}
     * Retorna o boletim do aluno como arquivo PDF.
     */
    @GetMapping("/boletim")
    @PreAuthorize("hasAnyAuthority('SECRETARIA', 'DIRETOR', 'ALUNO', 'ROLE_SECRETARIA', 'ROLE_DIRETOR', 'ROLE_ALUNO')")
    public ResponseEntity<byte[]> gerarBoletim(@RequestParam Long alunoId,
                                                @RequestParam Long turmaId,
                                                @RequestParam String periodo,
                                                @AuthenticationPrincipal String email,
                                                HttpServletRequest request) throws IOException {
        byte[] pdf = documentoService.gerarBoletim(alunoId, turmaId, periodo,
                email, obterIp(request));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"boletim_" + alunoId + "_" + periodo + ".pdf\"")
                .body(pdf);
    }

    /**
     * GET /api/v1/documentos/historico?alunoId={id}
     * Retorna o histórico escolar completo como PDF.
     */
    @GetMapping("/historico")
    @PreAuthorize("hasAnyAuthority('SECRETARIA', 'DIRETOR', 'ALUNO', 'ROLE_SECRETARIA', 'ROLE_DIRETOR', 'ROLE_ALUNO')")
    public ResponseEntity<byte[]> gerarHistorico(@RequestParam Long alunoId,
                                                  @AuthenticationPrincipal String email,
                                                  HttpServletRequest request) throws IOException {
        byte[] pdf = documentoService.gerarHistorico(alunoId,
                email, obterIp(request));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"historico_" + alunoId + ".pdf\"")
                .body(pdf);
    }

    private String obterIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        return (forwarded != null && !forwarded.isBlank())
                ? forwarded.split(",")[0].trim()
                : request.getRemoteAddr();
    }
}
