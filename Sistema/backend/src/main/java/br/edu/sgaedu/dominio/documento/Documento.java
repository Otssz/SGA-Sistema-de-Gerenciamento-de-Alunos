package br.edu.sgaedu.dominio.documento;

import br.edu.sgaedu.dominio.entidade.Aluno;
import java.time.LocalDateTime;

/**
 * Produto abstrato do Factory Method (ADR-004).
 * Subclasses: Boletim, Historico.
 * REGRA: sem imports de Spring nesta classe.
 */
public abstract class Documento {

    protected Long id;
    protected Aluno aluno;
    protected LocalDateTime geradoEm;
    protected String caminhoArquivo;

    protected Documento(Aluno aluno) {
        this.aluno = aluno;
        this.geradoEm = LocalDateTime.now();
    }

    /**
     * Template Method — cada subtipo define como serializa seu conteúdo em bytes PDF.
     *
     * @return array de bytes com o PDF gerado
     */
    public abstract byte[] gerarPdf();

    /**
     * Tipo do documento para log e nome de arquivo.
     */
    public abstract String getTipo();

    // --- Getters comuns ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Aluno getAluno() { return aluno; }

    public LocalDateTime getGeradoEm() { return geradoEm; }

    public String getCaminhoArquivo() { return caminhoArquivo; }
    public void setCaminhoArquivo(String caminho) { this.caminhoArquivo = caminho; }
}
