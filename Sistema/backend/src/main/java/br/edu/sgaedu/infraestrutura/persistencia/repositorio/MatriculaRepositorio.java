package br.edu.sgaedu.infraestrutura.persistencia.repositorio;

import br.edu.sgaedu.dominio.entidade.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MatriculaRepositorio extends JpaRepository<Matricula, Long> {

    Optional<Matricula> findByAlunoIdAndTurmaId(Long alunoId, Long turmaId);

    List<Matricula> findByAlunoId(Long alunoId);

    List<Matricula> findByTurmaIdAndStatus(Long turmaId, Matricula.Status status);

    boolean existsByAlunoIdAndTurmaId(Long alunoId, Long turmaId);
}
