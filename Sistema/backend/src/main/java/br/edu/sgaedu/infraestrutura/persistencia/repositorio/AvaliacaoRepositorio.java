package br.edu.sgaedu.infraestrutura.persistencia.repositorio;

import br.edu.sgaedu.dominio.entidade.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AvaliacaoRepositorio extends JpaRepository<Avaliacao, Long> {

    List<Avaliacao> findByTurmaIdOrderByDataAplicacaoAsc(Long turmaId);
}
