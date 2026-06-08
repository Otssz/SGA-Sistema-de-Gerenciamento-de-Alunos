package br.edu.sgaedu.dominio.fabrica;

import br.edu.sgaedu.dominio.documento.Documento;
import br.edu.sgaedu.dominio.documento.Historico;
import br.edu.sgaedu.dominio.entidade.Aluno;
import br.edu.sgaedu.dominio.entidade.Matricula;
import java.util.List;

/**
 * Criador Concreto B — fabrica Históricos escolares.
 */
public class HistoricoFactory extends DocumentoFactory {

    public HistoricoFactory(String diretorioBase) {
        super(diretorioBase);
    }

    /**
     * @param parametros [0] = List<Matricula> historicoDeCursos
     */
    @Override
    @SuppressWarnings("unchecked")
    public Documento criar(Aluno aluno, Object... parametros) {
        if (parametros.length < 1) {
            throw new IllegalArgumentException("HistoricoFactory requer: historicoDeCursos (List<Matricula>)");
        }
        List<Matricula> historico = (List<Matricula>) parametros[0];
        return new Historico(aluno, historico);
    }
}
