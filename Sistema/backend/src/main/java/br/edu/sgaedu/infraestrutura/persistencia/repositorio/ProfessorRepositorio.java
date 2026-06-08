package br.edu.sgaedu.infraestrutura.persistencia.repositorio;

import br.edu.sgaedu.dominio.entidade.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProfessorRepositorio extends JpaRepository<Professor, Long> {

    Optional<Professor> findByEmail(String email);

    Optional<Professor> findByRegistro(String registro);
}
