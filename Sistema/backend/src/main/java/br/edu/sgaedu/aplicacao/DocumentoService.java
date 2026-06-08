package br.edu.sgaedu.aplicacao;

import br.edu.sgaedu.dominio.documento.Documento;
import br.edu.sgaedu.dominio.entidade.Aluno;
import br.edu.sgaedu.dominio.entidade.Nota;
import br.edu.sgaedu.dominio.fabrica.BoletimFactory;
import br.edu.sgaedu.dominio.fabrica.HistoricoFactory;
import br.edu.sgaedu.infraestrutura.persistencia.repositorio.AlunoRepositorio;
import br.edu.sgaedu.infraestrutura.persistencia.repositorio.MatriculaRepositorio;
import br.edu.sgaedu.infraestrutura.persistencia.repositorio.NotaRepositorio;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Serviço de geração de documentos PDF (UC-04).
 * Usa Factory Method Pattern para criar Boletim ou Histórico.
 * REGRA: não contém lógica de HTTP (controllers fazem isso).
 */
@Service
@Transactional(readOnly = true)
public class DocumentoService {

    private final AlunoRepositorio alunoRepositorio;
    private final NotaRepositorio notaRepositorio;
    private final MatriculaRepositorio matriculaRepositorio;
    private final AuditoriaService auditoriaService;

    @Value("${sgaedu.storage.diretorio}")
    private String diretorioBase;

    public DocumentoService(AlunoRepositorio alunoRepositorio,
                             NotaRepositorio notaRepositorio,
                             MatriculaRepositorio matriculaRepositorio,
                             AuditoriaService auditoriaService) {
        this.alunoRepositorio = alunoRepositorio;
        this.notaRepositorio = notaRepositorio;
        this.matriculaRepositorio = matriculaRepositorio;
        this.auditoriaService = auditoriaService;
    }

    /**
     * Gera o boletim do aluno para um período letivo (UC-04).
     *
     * @param alunoId      ID do aluno
     * @param turmaId      ID da turma (período)
     * @param periodo      rótulo do período (ex.: "2024-1")
     * @param executorEmail email do solicitante (auditoria)
     * @param ip           IP do cliente
     * @return bytes do PDF gerado
     */
    @Transactional
    public byte[] gerarBoletim(Long alunoId, Long turmaId, String periodo,
                                String executorEmail, String ip) throws IOException {
        Aluno aluno = alunoRepositorio.findById(alunoId)
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado: " + alunoId));

        List<Nota> notas = notaRepositorio.findByAlunoIdAndTurmaId(alunoId, turmaId);

        BoletimFactory fabrica = new BoletimFactory(diretorioBase);
        Documento boletim = fabrica.criarEDefinirCaminho(aluno, periodo, notas);

        byte[] pdf = boletim.gerarPdf();
        salvarEmDisco(boletim.getCaminhoArquivo(), pdf);

        auditoriaService.registrar("BOLETIM_GERADO", "Boletim", null, executorEmail, ip,
                "{\"alunoId\":" + alunoId + ",\"periodo\":\"" + periodo + "\"}");

        return pdf;
    }

    /**
     * Gera o histórico escolar completo do aluno (UC-04).
     *
     * @param alunoId      ID do aluno
     * @param executorEmail email do solicitante (auditoria)
     * @param ip           IP do cliente
     * @return bytes do PDF gerado
     */
    @Transactional
    public byte[] gerarHistorico(Long alunoId, String executorEmail, String ip) throws IOException {
        Aluno aluno = alunoRepositorio.findById(alunoId)
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado: " + alunoId));

        var historico = matriculaRepositorio.findByAlunoId(alunoId);

        HistoricoFactory fabrica = new HistoricoFactory(diretorioBase);
        Documento doc = fabrica.criarEDefinirCaminho(aluno, historico);

        byte[] pdf = doc.gerarPdf();
        salvarEmDisco(doc.getCaminhoArquivo(), pdf);

        auditoriaService.registrar("HISTORICO_GERADO", "Historico", null, executorEmail, ip,
                "{\"alunoId\":" + alunoId + "}");

        return pdf;
    }

    private void salvarEmDisco(String caminho, byte[] conteudo) throws IOException {
        Path path = Path.of(caminho);
        Files.createDirectories(path.getParent());
        Files.write(path, conteudo);
    }
}
