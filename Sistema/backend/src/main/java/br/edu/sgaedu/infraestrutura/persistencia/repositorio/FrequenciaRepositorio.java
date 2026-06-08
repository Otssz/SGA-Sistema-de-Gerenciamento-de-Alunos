package br.edu.sgaedu.infraestrutura.persistencia.repositorio;

import br.edu.sgaedu.dominio.entidade.Frequencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface FrequenciaRepositorio extends JpaRepository<Frequencia, Long> {

    List<Frequencia> findByAlunoIdAndTurmaIdOrderByDataAulaDesc(Long alunoId, Long turmaId);

    boolean existsByAlunoIdAndTurmaIdAndDataAula(Long alunoId, Long turmaId, LocalDate dataAula);

    long countByAlunoIdAndTurmaId(Long alunoId, Long turmaId);

    long countByAlunoIdAndTurmaIdAndPresenteTrue(Long alunoId, Long turmaId);
}
