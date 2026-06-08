package br.edu.sgaedu.infraestrutura.persistencia.repositorio;

import br.edu.sgaedu.dominio.entidade.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CursoRepositorio extends JpaRepository<Curso, Long> {
    boolean existsByNome(String nome);

    Optional<Curso> findByNome(String nome);
}
