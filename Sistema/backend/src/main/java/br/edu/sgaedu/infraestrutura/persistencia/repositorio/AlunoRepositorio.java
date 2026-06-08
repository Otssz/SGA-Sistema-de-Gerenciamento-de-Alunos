package br.edu.sgaedu.infraestrutura.persistencia.repositorio;

import br.edu.sgaedu.dominio.entidade.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AlunoRepositorio extends JpaRepository<Aluno, Long> {

    Optional<Aluno> findByEmail(String email);

    Optional<Aluno> findByMatricula(String matricula);

    boolean existsByEmail(String email);

    boolean existsByMatricula(String matricula);

    /** Carrega aluno com responsáveis em uma query (evita N+1). */
    @Query("SELECT a FROM Aluno a LEFT JOIN FETCH a.responsaveis WHERE a.id = :id")
    Optional<Aluno> findByIdComResponsaveis(@Param("id") Long id);
}
