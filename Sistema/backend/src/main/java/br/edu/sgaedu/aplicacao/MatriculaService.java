package br.edu.sgaedu.aplicacao;

import br.edu.sgaedu.api.dto.MatricularAlunoDTO;
import br.edu.sgaedu.dominio.command.CancelarMatriculaCommand;
import br.edu.sgaedu.dominio.command.ComandoInvoker;
import br.edu.sgaedu.dominio.command.MatricularAlunoCommand;
import br.edu.sgaedu.dominio.entidade.Aluno;
import br.edu.sgaedu.dominio.entidade.Matricula;
import br.edu.sgaedu.dominio.entidade.Turma;
import br.edu.sgaedu.dominio.observer.MatriculaConfirmada;
import br.edu.sgaedu.infraestrutura.persistencia.repositorio.AlunoRepositorio;
import br.edu.sgaedu.infraestrutura.persistencia.repositorio.MatriculaRepositorio;
import br.edu.sgaedu.infraestrutura.persistencia.repositorio.TurmaRepositorio;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Serviço de matrículas (UC-01).
 * Usa o padrão Command para encapsular as operações de matricular e cancelar,
 * suportando desfazer caso ocorra erro no fluxo.
 */
@Service
@Transactional(readOnly = true)
public class MatriculaService {

    private final MatriculaRepositorio matriculaRepositorio;
    private final AlunoRepositorio alunoRepositorio;
    private final TurmaRepositorio turmaRepositorio;
    private final AuditoriaService auditoriaService;
    private final NotificacaoService notificacaoService;

    public MatriculaService(MatriculaRepositorio matriculaRepositorio,
                             AlunoRepositorio alunoRepositorio,
                             TurmaRepositorio turmaRepositorio,
                             AuditoriaService auditoriaService,
                             NotificacaoService notificacaoService) {
        this.matriculaRepositorio = matriculaRepositorio;
        this.alunoRepositorio = alunoRepositorio;
        this.turmaRepositorio = turmaRepositorio;
        this.auditoriaService = auditoriaService;
        this.notificacaoService = notificacaoService;
    }

    /**
     * Matricula um aluno em uma turma usando o Command Pattern (UC-01).
     *
     * @param dto          dados da matrícula (alunoId, turmaId)
     * @param executorEmail email do secretário/coordenador autenticado
     * @param ip           IP do cliente
     * @return matrícula criada
     */
    @Transactional
    public Matricula matricular(MatricularAlunoDTO dto, String executorEmail, String ip) {
        Aluno aluno = alunoRepositorio.findById(dto.alunoId())
                .orElseThrow(() -> new EntityNotFoundException("Aluno não encontrado: " + dto.alunoId()));

        Turma turma = turmaRepositorio.findById(dto.turmaId())
                .orElseThrow(() -> new EntityNotFoundException("Turma não encontrada: " + dto.turmaId()));

        if (matriculaRepositorio.existsByAlunoIdAndTurmaId(aluno.getId(), turma.getId())) {
            throw new IllegalStateException("Aluno já matriculado nesta turma.");
        }

        // Command Pattern — encapsula a operação e suporta desfazer
        ComandoInvoker invoker = new ComandoInvoker();
        MatricularAlunoCommand comando = new MatricularAlunoCommand(
                aluno, turma,
                matriculaRepositorio::save,
                m -> {
                    m.setStatus(Matricula.Status.CANCELADA);
                    matriculaRepositorio.save(m);
                }
        );

        invoker.executar(comando);
        Matricula matriculaCriada = comando.getMatriculaCriada();

        // Observer — confirma matrícula
        notificacaoService.publicar(new MatriculaConfirmada(matriculaCriada));

        // Auditoria
        auditoriaService.registrar("MATRICULA_CRIADA", "Matricula",
                matriculaCriada.getId(), executorEmail, ip);

        return matriculaCriada;
    }

    /**
     * Cancela uma matrícula ativa (UC-01 — fluxo alternativo).
     *
     * @param matriculaId  ID da matrícula a cancelar
     * @param motivo       justificativa de cancelamento
     * @param executorEmail email do secretário/coordenador
     * @param ip           IP do cliente
     */
    @Transactional
    public void cancelar(Long matriculaId, String motivo, String executorEmail, String ip) {
        Matricula matricula = matriculaRepositorio.findById(matriculaId)
                .orElseThrow(() -> new EntityNotFoundException("Matrícula não encontrada: " + matriculaId));

        ComandoInvoker invoker = new ComandoInvoker();
        CancelarMatriculaCommand comando = new CancelarMatriculaCommand(
                matricula, motivo, matriculaRepositorio::save
        );

        invoker.executar(comando);

        auditoriaService.registrar("MATRICULA_CANCELADA", "Matricula",
                matriculaId, executorEmail, ip);
    }

    public List<Matricula> listarPorAluno(Long alunoId) {
        return matriculaRepositorio.findByAlunoId(alunoId);
    }

    public List<Matricula> listarTodas() {
        return matriculaRepositorio.findAll();
    }

    public List<Matricula> listarAtivasPorTurma(Long turmaId) {
        return matriculaRepositorio.findByTurmaIdAndStatus(turmaId, Matricula.Status.ATIVA);
    }
}
