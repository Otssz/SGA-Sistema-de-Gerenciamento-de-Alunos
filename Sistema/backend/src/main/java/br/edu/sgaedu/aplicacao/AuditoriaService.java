package br.edu.sgaedu.aplicacao;

import br.edu.sgaedu.dominio.entidade.LogAuditoria;
import br.edu.sgaedu.infraestrutura.persistencia.repositorio.LogAuditoriaRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço transversal de auditoria — deve ser chamado por TODAS as operações sensíveis.
 * REGRA: toda ação envolvendo dados pessoais (LGPD) gera um LogAuditoria.
 *
 * Usa PROPAGATION.REQUIRES_NEW para garantir que o log seja persistido mesmo que
 * a transação chamadora seja revertida.
 */
@Service
public class AuditoriaService {

    private final LogAuditoriaRepositorio logRepositorio;

    public AuditoriaService(LogAuditoriaRepositorio logRepositorio) {
        this.logRepositorio = logRepositorio;
    }

    /**
     * Registra uma ação auditável.
     *
     * @param acao         código da ação (ex.: "NOTA_LANCADA")
     * @param entidade     nome da entidade afetada (ex.: "Nota")
     * @param entidadeId   ID do registro afetado (pode ser null)
     * @param executadoPor email do usuário responsável
     * @param ip           endereço IP do cliente (extraído do request)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(String acao, String entidade, Long entidadeId,
                          String executadoPor, String ip) {
        LogAuditoria log = LogAuditoria.criar(acao, entidade, entidadeId, executadoPor, ip);
        logRepositorio.save(log);
    }

    /**
     * Sobrecarga com detalhes adicionais em JSON.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(String acao, String entidade, Long entidadeId,
                          String executadoPor, String ip, String detalhesJson) {
        LogAuditoria log = LogAuditoria.criar(acao, entidade, entidadeId, executadoPor, ip);
        log.setDetalhes(detalhesJson);
        logRepositorio.save(log);
    }
}
