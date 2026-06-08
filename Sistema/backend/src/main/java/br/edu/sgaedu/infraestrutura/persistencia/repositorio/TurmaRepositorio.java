package br.edu.sgaedu.infraestrutura.persistencia.repositorio;

import br.edu.sgaedu.dominio.entidade.Turma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TurmaRepositorio extends JpaRepository<Turma, Long> {

    List<Turma> findByAnoLetivo(String anoLetivo);

    Optional<Turma> findByCodigo(String codigo);

    /** Turmas de um professor (para filtro RBAC no lançamento de notas). */
    @Query("SELECT t FROM Turma t JOIN t.professores p WHERE p.id = :professorId")
    List<Turma> findByProfessorId(@Param("professorId") Long professorId);
}
