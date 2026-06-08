package br.edu.sgaedu.api.controller;

import br.edu.sgaedu.api.dto.AvaliacaoResumoDTO;
import br.edu.sgaedu.api.dto.AlunoResumoDTO;
import br.edu.sgaedu.api.dto.CadastrarAvaliacaoDTO;
import br.edu.sgaedu.api.dto.CadastrarTurmaDTO;
import br.edu.sgaedu.api.dto.TurmaResumoDTO;
import br.edu.sgaedu.dominio.entidade.Avaliacao;
import br.edu.sgaedu.dominio.entidade.Aluno;
import br.edu.sgaedu.dominio.entidade.Curso;
import br.edu.sgaedu.dominio.entidade.Matricula;
import br.edu.sgaedu.dominio.entidade.Turma;
import br.edu.sgaedu.infraestrutura.persistencia.repositorio.AvaliacaoRepositorio;
import br.edu.sgaedu.infraestrutura.persistencia.repositorio.CursoRepositorio;
import br.edu.sgaedu.infraestrutura.persistencia.repositorio.MatriculaRepositorio;
import br.edu.sgaedu.infraestrutura.persistencia.repositorio.TurmaRepositorio;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/turmas")
public class TurmaController {

    private final TurmaRepositorio turmaRepositorio;
    private final AvaliacaoRepositorio avaliacaoRepositorio;
    private final CursoRepositorio cursoRepositorio;
    private final MatriculaRepositorio matriculaRepositorio;

    public TurmaController(TurmaRepositorio turmaRepositorio,
                           AvaliacaoRepositorio avaliacaoRepositorio,
                           CursoRepositorio cursoRepositorio,
                           MatriculaRepositorio matriculaRepositorio) {
        this.turmaRepositorio = turmaRepositorio;
        this.avaliacaoRepositorio = avaliacaoRepositorio;
        this.cursoRepositorio = cursoRepositorio;
        this.matriculaRepositorio = matriculaRepositorio;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SECRETARIA', 'DIRETOR', 'PROFESSOR', 'ALUNO', 'ROLE_SECRETARIA', 'ROLE_DIRETOR', 'ROLE_PROFESSOR', 'ROLE_ALUNO')")
    public ResponseEntity<List<TurmaResumoDTO>> listarTurmas() {
        List<TurmaResumoDTO> turmas = turmaRepositorio.findAll().stream()
                .sorted(Comparator.comparing(Turma::getCodigo, String.CASE_INSENSITIVE_ORDER))
                .map(this::toResumo)
                .toList();
        return ResponseEntity.ok(turmas);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SECRETARIA', 'DIRETOR', 'ROLE_SECRETARIA', 'ROLE_DIRETOR')")
    public ResponseEntity<TurmaResumoDTO> cadastrarTurma(@Valid @RequestBody CadastrarTurmaDTO dto) {
        if (turmaRepositorio.findByCodigo(dto.codigo()).isPresent()) {
            throw new IllegalStateException("Ja existe turma com este codigo.");
        }

        Curso curso = cursoRepositorio.findByNome(dto.curso())
                .orElseGet(() -> criarCurso(dto));

        Turma turma = new Turma();
        turma.setCodigo(dto.codigo());
        turma.setAnoLetivo(dto.anoLetivo());
        turma.setTurno(dto.turno());
        turma.setCurso(curso);

        Turma salva = turmaRepositorio.save(turma);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResumo(salva));
    }

    @GetMapping("/{turmaId}/avaliacoes")
    @PreAuthorize("hasAnyAuthority('SECRETARIA', 'DIRETOR', 'PROFESSOR', 'ROLE_SECRETARIA', 'ROLE_DIRETOR', 'ROLE_PROFESSOR')")
    public ResponseEntity<List<AvaliacaoResumoDTO>> listarAvaliacoes(@PathVariable Long turmaId) {
        List<AvaliacaoResumoDTO> avaliacoes = avaliacaoRepositorio.findByTurmaIdOrderByDataAplicacaoAsc(turmaId).stream()
                .map(this::toResumo)
                .toList();
        return ResponseEntity.ok(avaliacoes);
    }

    @GetMapping("/{turmaId}/alunos")
    @PreAuthorize("hasAnyAuthority('SECRETARIA', 'DIRETOR', 'PROFESSOR', 'ROLE_SECRETARIA', 'ROLE_DIRETOR', 'ROLE_PROFESSOR')")
    public ResponseEntity<List<AlunoResumoDTO>> listarAlunosDaTurma(@PathVariable Long turmaId) {
        List<AlunoResumoDTO> alunos = matriculaRepositorio
                .findByTurmaIdAndStatus(turmaId, Matricula.Status.ATIVA)
                .stream()
                .map(Matricula::getAluno)
                .sorted(Comparator.comparing(Aluno::getNome, String.CASE_INSENSITIVE_ORDER))
                .map(aluno -> new AlunoResumoDTO(aluno.getId(), aluno.getNome(), aluno.getEmail(), aluno.getMatricula()))
                .toList();
        return ResponseEntity.ok(alunos);
    }

    @PostMapping("/{turmaId}/avaliacoes")
    @PreAuthorize("hasAnyAuthority('PROFESSOR', 'DIRETOR', 'ROLE_PROFESSOR', 'ROLE_DIRETOR')")
    public ResponseEntity<AvaliacaoResumoDTO> cadastrarAvaliacao(@PathVariable Long turmaId,
                                                                  @Valid @RequestBody CadastrarAvaliacaoDTO dto) {
        Turma turma = turmaRepositorio.findById(turmaId)
                .orElseThrow(() -> new EntityNotFoundException("Turma nao encontrada: " + turmaId));

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setDescricao(dto.descricao());
        avaliacao.setDataAplicacao(dto.dataAplicacao());
        avaliacao.setPeso(dto.peso());
        avaliacao.setValorMaximo(dto.valorMaximo());
        avaliacao.setTurma(turma);

        Avaliacao salva = avaliacaoRepositorio.save(avaliacao);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResumo(salva));
    }

    private TurmaResumoDTO toResumo(Turma turma) {
        String curso = turma.getCurso() == null ? "-" : turma.getCurso().getNome();
        return new TurmaResumoDTO(turma.getId(), turma.getCodigo(), turma.getAnoLetivo(), turma.getTurno(), curso);
    }

    private Curso criarCurso(CadastrarTurmaDTO dto) {
        Curso curso = new Curso();
        curso.setNome(dto.curso());
        curso.setDescricao(dto.curso());
        curso.setCargaHorariaTotal(dto.cargaHorariaTotal());
        return cursoRepositorio.save(curso);
    }

    private AvaliacaoResumoDTO toResumo(Avaliacao avaliacao) {
        Turma turma = avaliacao.getTurma();
        Long turmaId = turma == null ? null : turma.getId();
        String codigoTurma = turma == null ? "-" : turma.getCodigo();
        return new AvaliacaoResumoDTO(
                avaliacao.getId(),
                avaliacao.getDescricao(),
                turmaId,
                codigoTurma,
                avaliacao.getPeso(),
                avaliacao.getValorMaximo()
        );
    }
}
