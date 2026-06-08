package br.edu.sgaedu.dominio.entidade;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Registro imutável de operação sensível — gravado por AuditoriaService.
 * LGPD: toda ação de leitura/escrita em dados pessoais deve gerar um log.
 * REGRA: nunca deletar ou alterar registros desta tabela (append-only).
 */
@Entity
@Table(name = "logs_auditoria")
public class LogAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Ex.: "NOTA_LANCADA", "MATRICULA_CRIADA", "DOCUMENTO_GERADO". */
    @Column(nullable = false, length = 50)
    private String acao;

    /** Entidade afetada (ex.: "Nota", "Matricula"). */
    @Column(nullable = false, length = 50)
    private String entidade;

    /** ID do registro afetado. */
    @Column
    private Long entidadeId;

    /** Email do usuário que executou a ação. */
    @Column(nullable = false, length = 200)
    private String executadoPor;

    /** IP do cliente (coletado pelo filtro HTTP). */
    @Column(length = 45)
    private String enderecoIp;

    /** Detalhes adicionais em formato JSON (pode ser null). */
    @Column(columnDefinition = "TEXT")
    private String detalhes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime ocorridoEm;

    @PrePersist
    private void definirData() {
        this.ocorridoEm = LocalDateTime.now();
    }

    // Construtor estático de conveniência
    public static LogAuditoria criar(String acao, String entidade, Long entidadeId,
                                     String executadoPor, String ip) {
        LogAuditoria log = new LogAuditoria();
        log.acao = acao;
        log.entidade = entidade;
        log.entidadeId = entidadeId;
        log.executadoPor = executadoPor;
        log.enderecoIp = ip;
        return log;
    }

    // --- Getters (sem setters — imutável após criação) ---

    public Long getId() { return id; }
    public String getAcao() { return acao; }
    public String getEntidade() { return entidade; }
    public Long getEntidadeId() { return entidadeId; }
    public String getExecutadoPor() { return executadoPor; }
    public String getEnderecoIp() { return enderecoIp; }
    public String getDetalhes() { return detalhes; }
    public void setDetalhes(String detalhes) { this.detalhes = detalhes; }
    public LocalDateTime getOcorridoEm() { return ocorridoEm; }
}
