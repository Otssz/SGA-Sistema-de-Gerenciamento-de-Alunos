package br.edu.sgaedu.aplicacao;

import br.edu.sgaedu.dominio.entidade.Aluno;
import br.edu.sgaedu.dominio.entidade.Matricula;
import br.edu.sgaedu.dominio.entidade.Nota;
import br.edu.sgaedu.dominio.entidade.Turma;
import br.edu.sgaedu.dominio.observer.RiscoReprovacao;
import br.edu.sgaedu.dominio.strategy.MediaStrategy;
import br.edu.sgaedu.infraestrutura.persistencia.repositorio.MatriculaRepositorio;
import br.edu.sgaedu.infraestrutura.persistencia.repositorio.NotaRepositorio;
import br.edu.sgaedu.infraestrutura.persistencia.repositorio.TurmaRepositorio;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

/**
 * Servico de calculo de medias (UC-03) usando Strategy.
 */
@Service
@Transactional(readOnly = true)
public class MediaService {

    private static final double LIMIAR_REPROVACAO = 5.0;

    private final NotaRepositorio notaRepositorio;
    private final MatriculaRepositorio matriculaRepositorio;
    private final TurmaRepositorio turmaRepositorio;
    private final NotificacaoService notificacaoService;
    private final Map<String, MediaStrategy> estrategiasPorCurso;
    private final MediaStrategy estrategiaPadrao;

    public MediaService(NotaRepositorio notaRepositorio,
                        MatriculaRepositorio matriculaRepositorio,
                        TurmaRepositorio turmaRepositorio,
                        NotificacaoService notificacaoService,
                        Map<String, MediaStrategy> estrategiasPorCurso,
                        MediaStrategy estrategiaPadrao) {
        this.notaRepositorio = notaRepositorio;
        this.matriculaRepositorio = matriculaRepositorio;
        this.turmaRepositorio = turmaRepositorio;
        this.notificacaoService = notificacaoService;
        this.estrategiasPorCurso = estrategiasPorCurso;
        this.estrategiaPadrao = estrategiaPadrao;
    }

    public double calcularMedia(Long alunoId, Long turmaId) {
        Turma turma = turmaRepositorio.findById(turmaId)
                .orElseThrow(() -> new EntityNotFoundException("Turma nao encontrada: " + turmaId));
        List<Nota> notas = notaRepositorio.findByAlunoIdAndTurmaId(alunoId, turmaId);
        return resolverEstrategia(turma).calcular(notas);
    }

    @Scheduled(cron = "0 0 3 * * *", zone = "America/Sao_Paulo")
    @Transactional
    public void verificarRiscoReprovacaoGlobal() {
        matriculaRepositorio.findAll().stream()
                .filter(matricula -> matricula.getStatus() == Matricula.Status.ATIVA)
                .forEach(matricula -> verificarRiscoReprovacao(matricula.getAluno(), matricula.getTurma()));
    }

    public void verificarRiscoReprovacao(Aluno aluno, Turma turma) {
        double media = calcularMedia(aluno.getId(), turma.getId());
        if (media < LIMIAR_REPROVACAO) {
            notificacaoService.publicar(new RiscoReprovacao(aluno, turma, media, LIMIAR_REPROVACAO));
        }
    }

    private MediaStrategy resolverEstrategia(Turma turma) {
        if (turma.getCurso() == null || turma.getCurso().getNome() == null) {
            return estrategiaPadrao;
        }
        return estrategiasPorCurso.getOrDefault(turma.getCurso().getNome(), estrategiaPadrao);
    }
}
