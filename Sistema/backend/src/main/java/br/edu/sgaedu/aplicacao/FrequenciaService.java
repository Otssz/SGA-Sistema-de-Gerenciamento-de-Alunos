package br.edu.sgaedu.aplicacao;

import br.edu.sgaedu.api.dto.FrequenciaResumoDTO;
import br.edu.sgaedu.api.dto.RegistrarFrequenciaDTO;
import br.edu.sgaedu.dominio.entidade.Aluno;
import br.edu.sgaedu.dominio.entidade.Frequencia;
import br.edu.sgaedu.dominio.entidade.Professor;
import br.edu.sgaedu.dominio.entidade.Turma;
import br.edu.sgaedu.infraestrutura.persistencia.repositorio.AlunoRepositorio;
import br.edu.sgaedu.infraestrutura.persistencia.repositorio.FrequenciaRepositorio;
import br.edu.sgaedu.infraestrutura.persistencia.repositorio.ProfessorRepositorio;
import br.edu.sgaedu.infraestrutura.persistencia.repositorio.TurmaRepositorio;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço de controle de frequência (UC-05).
 * RN-001: frequência mínima de 75% — alunos abaixo são reprovados por falta.
 */
@Service
@Transactional(readOnly = true)
public class FrequenciaService {

    private static final double FREQUENCIA_MINIMA = 75.0;

    private final FrequenciaRepositorio frequenciaRepositorio;
    private final AlunoRepositorio alunoRepositorio;
    private final TurmaRepositorio turmaRepositorio;
    private final ProfessorRepositorio professorRepositorio;
    private final AuditoriaService auditoriaService;

    public FrequenciaService(FrequenciaRepositorio frequenciaRepositorio,
                             AlunoRepositorio alunoRepositorio,
                             TurmaRepositorio turmaRepositorio,
                             ProfessorRepositorio professorRepositorio,
                             AuditoriaService auditoriaService) {
        this.frequenciaRepositorio = frequenciaRepositorio;
        this.alunoRepositorio = alunoRepositorio;
        this.turmaRepositorio = turmaRepositorio;
        this.professorRepositorio = professorRepositorio;
        this.auditoriaService = auditoriaService;
    }

    @Transactional
    public Frequencia registrar(Long turmaId, RegistrarFrequenciaDTO dto,
                                String professorEmail, String ip) {
        Aluno aluno = alunoRepositorio.findById(dto.alunoId())
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado: " + dto.alunoId()));

        Turma turma = turmaRepositorio.findById(turmaId)
                .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada: " + turmaId));

        Professor professor = professorRepositorio.findByEmail(professorEmail)
                .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado: " + professorEmail));

        if (frequenciaRepositorio.existsByAlunoIdAndTurmaIdAndDataAula(
                aluno.getId(), turmaId, dto.data())) {
            throw new IllegalStateException(
                    "Frequência já registrada para este aluno na data " + dto.data());
        }

        Frequencia frequencia = new Frequencia();
        frequencia.setAluno(aluno);
        frequencia.setTurma(turma);
        frequencia.setDataAula(dto.data());
        frequencia.setPresente(dto.presente());
        frequencia.setJustificativa(dto.justificativa());
        frequencia.setRegistradoPor(professor);

        Frequencia salva = frequenciaRepositorio.save(frequencia);

        auditoriaService.registrar("FREQUENCIA_REGISTRADA", "Frequencia",
                salva.getId(), professorEmail, ip);

        return salva;
    }

    /** Resumo de frequência de um aluno em uma turma (RN-001). */
    public FrequenciaResumoDTO resumo(Long turmaId, Long alunoId) {
        long total = frequenciaRepositorio.countByAlunoIdAndTurmaId(alunoId, turmaId);
        long presencas = frequenciaRepositorio.countByAlunoIdAndTurmaIdAndPresenteTrue(alunoId, turmaId);
        long faltas = total - presencas;
        double percentual = total == 0 ? 0.0 : (presencas * 100.0) / total;
        String situacao = percentual >= FREQUENCIA_MINIMA ? "APROVADO" : "RISCO DE REPROVAÇÃO";

        return new FrequenciaResumoDTO(total, presencas, faltas, percentual, situacao);
    }
}
