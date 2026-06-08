package br.edu.sgaedu.aplicacao;

import br.edu.sgaedu.api.dto.LancarNotaDTO;
import br.edu.sgaedu.dominio.entidade.Aluno;
import br.edu.sgaedu.dominio.entidade.Avaliacao;
import br.edu.sgaedu.dominio.entidade.Matricula;
import br.edu.sgaedu.dominio.entidade.Nota;
import br.edu.sgaedu.dominio.entidade.Professor;
import br.edu.sgaedu.dominio.observer.NotaLancada;
import br.edu.sgaedu.infraestrutura.persistencia.repositorio.AlunoRepositorio;
import br.edu.sgaedu.infraestrutura.persistencia.repositorio.AvaliacaoRepositorio;
import br.edu.sgaedu.infraestrutura.persistencia.repositorio.MatriculaRepositorio;
import br.edu.sgaedu.infraestrutura.persistencia.repositorio.NotaRepositorio;
import br.edu.sgaedu.infraestrutura.persistencia.repositorio.ProfessorRepositorio;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class NotaService {

    private final NotaRepositorio notaRepositorio;
    private final AlunoRepositorio alunoRepositorio;
    private final AvaliacaoRepositorio avaliacaoRepositorio;
    private final MatriculaRepositorio matriculaRepositorio;
    private final ProfessorRepositorio professorRepositorio;
    private final AuditoriaService auditoriaService;
    private final NotificacaoService notificacaoService;

    public NotaService(NotaRepositorio notaRepositorio,
                       AlunoRepositorio alunoRepositorio,
                       AvaliacaoRepositorio avaliacaoRepositorio,
                       MatriculaRepositorio matriculaRepositorio,
                       ProfessorRepositorio professorRepositorio,
                       AuditoriaService auditoriaService,
                       NotificacaoService notificacaoService) {
        this.notaRepositorio = notaRepositorio;
        this.alunoRepositorio = alunoRepositorio;
        this.avaliacaoRepositorio = avaliacaoRepositorio;
        this.matriculaRepositorio = matriculaRepositorio;
        this.professorRepositorio = professorRepositorio;
        this.auditoriaService = auditoriaService;
        this.notificacaoService = notificacaoService;
    }

    @Transactional
    public Nota lancarNota(Long turmaId, LancarNotaDTO dto, String professorEmail, String ip) {
        Aluno aluno = alunoRepositorio.findById(dto.alunoId())
                .orElseThrow(() -> new EntityNotFoundException("Aluno nao encontrado: " + dto.alunoId()));

        Avaliacao avaliacao = avaliacaoRepositorio.findById(dto.avaliacaoId())
                .orElseThrow(() -> new EntityNotFoundException("Avaliacao nao encontrada: " + dto.avaliacaoId()));

        Professor professor = professorRepositorio.findByEmail(professorEmail)
                .orElseThrow(() -> new EntityNotFoundException("Professor nao encontrado: " + professorEmail));

        if (avaliacao.getTurma() == null || !avaliacao.getTurma().getId().equals(turmaId)) {
            throw new IllegalStateException("Avaliacao " + avaliacao.getId()
                    + " nao pertence a turma selecionada.");
        }

        if (!matriculaRepositorio.existsByAlunoIdAndTurmaId(aluno.getId(), turmaId)) {
            throw new IllegalStateException("Aluno " + aluno.getId()
                    + " nao esta matriculado na turma selecionada.");
        }

        if (notaRepositorio.existsByAlunoIdAndAvaliacaoId(aluno.getId(), avaliacao.getId())) {
            throw new IllegalStateException("Nota ja lancada para este aluno nesta avaliacao.");
        }

        Nota nota = new Nota();
        nota.setAluno(aluno);
        nota.setAvaliacao(avaliacao);
        nota.setValor(dto.valor());
        nota.setObservacao(dto.observacao());
        nota.setLancadaPor(professor);

        Nota notaSalva = notaRepositorio.save(nota);
        notificacaoService.publicar(new NotaLancada(notaSalva, professor));
        auditoriaService.registrar("NOTA_LANCADA", "Nota", notaSalva.getId(), professorEmail, ip);

        return notaSalva;
    }

    public List<Nota> buscarNotasPorAlunoETurma(Long alunoId, Long turmaId) {
        return notaRepositorio.findByAlunoIdAndTurmaId(alunoId, turmaId, Matricula.Status.ATIVA);
    }

    public List<Nota> buscarNotasPorTurma(Long turmaId) {
        return notaRepositorio.findByTurmaId(turmaId, Matricula.Status.ATIVA);
    }
}
