package br.edu.sgaedu.infraestrutura.persistencia.repositorio;

import br.edu.sgaedu.dominio.entidade.LogAuditoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

/** Repositório append-only — nunca deletar registros desta tabela. */
@Repository
public interface LogAuditoriaRepositorio extends JpaRepository<LogAuditoria, Long> {

    Page<LogAuditoria> findByExecutadoPor(String email, Pageable pageable);

    Page<LogAuditoria> findByEntidade(String entidade, Pageable pageable);

    Page<LogAuditoria> findByOcorridoEmBetween(LocalDateTime inicio,
                                                LocalDateTime fim,
                                                Pageable pageable);
}
