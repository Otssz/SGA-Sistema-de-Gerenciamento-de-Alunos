package br.edu.sgaedu.infraestrutura.persistencia.repositorio;

import br.edu.sgaedu.dominio.entidade.Nota;
import br.edu.sgaedu.dominio.entidade.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotaRepositorio extends JpaRepository<Nota, Long> {

    @Query("""
        SELECT n FROM Nota n
        JOIN n.avaliacao av
        JOIN Matricula m ON m.aluno.id = n.aluno.id AND m.turma.id = av.turma.id
        WHERE n.aluno.id = :alunoId
        AND av.turma.id = :turmaId
        AND m.status = :status
        ORDER BY av.dataAplicacao
    """)
    List<Nota> findByAlunoIdAndTurmaId(@Param("alunoId") Long alunoId,
                                        @Param("turmaId") Long turmaId,
                                        @Param("status") Matricula.Status status);

    default List<Nota> findByAlunoIdAndTurmaId(Long alunoId, Long turmaId) {
        return findByAlunoIdAndTurmaId(alunoId, turmaId, Matricula.Status.ATIVA);
    }

    @Query("""
        SELECT n FROM Nota n
        JOIN n.avaliacao av
        JOIN n.aluno a
        JOIN Matricula m ON m.aluno.id = a.id AND m.turma.id = av.turma.id
        WHERE av.turma.id = :turmaId
        AND m.status = :status
        ORDER BY a.nome, av.dataAplicacao
    """)
    List<Nota> findByTurmaId(@Param("turmaId") Long turmaId,
                             @Param("status") Matricula.Status status);

    default List<Nota> findByTurmaId(Long turmaId) {
        return findByTurmaId(turmaId, Matricula.Status.ATIVA);
    }

    List<Nota> findByAvaliacaoId(Long avaliacaoId);

    boolean existsByAlunoIdAndAvaliacaoId(Long alunoId, Long avaliacaoId);
}
