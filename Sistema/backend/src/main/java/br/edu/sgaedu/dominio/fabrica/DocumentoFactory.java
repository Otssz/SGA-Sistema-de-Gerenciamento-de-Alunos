package br.edu.sgaedu.dominio.fabrica;

import br.edu.sgaedu.dominio.documento.Documento;
import br.edu.sgaedu.dominio.entidade.Aluno;

/**
 * Criador abstrato do Factory Method (ADR-004).
 * Subclasses: BoletimFactory, HistoricoFactory.
 *
 * Mantém o método Template criarEArmazenar() com o passo comum de
 * definir o caminho do arquivo — as fábricas concretas apenas implementam criar().
 */
public abstract class DocumentoFactory {

    private final String diretorioBase;

    protected DocumentoFactory(String diretorioBase) {
        this.diretorioBase = diretorioBase;
    }

    /**
     * Factory Method — cria o documento específico para o aluno.
     * Implementado pelas subclasses.
     */
    public abstract Documento criar(Aluno aluno, Object... parametros);

    /**
     * Template Method — cria o documento e define o caminho de armazenamento.
     *
     * @param aluno       aluno dono do documento
     * @param parametros  parâmetros específicos de cada tipo
     * @return documento com caminho já definido (pronto para salvar em disco)
     */
    public Documento criarEDefinirCaminho(Aluno aluno, Object... parametros) {
        Documento documento = criar(aluno, parametros);
        String caminho = diretorioBase + "/" + aluno.getMatricula()
                + "/" + documento.getTipo().toLowerCase()
                + "_" + documento.getGeradoEm().toLocalDate()
                + ".pdf";
        documento.setCaminhoArquivo(caminho);
        return documento;
    }

    protected String getDiretorioBase() { return diretorioBase; }
}
