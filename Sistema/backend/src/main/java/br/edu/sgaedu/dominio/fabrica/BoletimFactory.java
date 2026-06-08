package br.edu.sgaedu.dominio.fabrica;

import br.edu.sgaedu.dominio.documento.Boletim;
import br.edu.sgaedu.dominio.documento.Documento;
import br.edu.sgaedu.dominio.entidade.Aluno;
import br.edu.sgaedu.dominio.entidade.Nota;
import java.util.List;

/**
 * Criador Concreto A — fabrica Boletins.
 * Instanciado pelo DocumentoService via injeção de dependência.
 */
public class BoletimFactory extends DocumentoFactory {

    public BoletimFactory(String diretorioBase) {
        super(diretorioBase);
    }

    /**
     * @param parametros [0] = String periodo (ex.: "2024-1"), [1] = List<Nota> notas
     */
    @Override
    @SuppressWarnings("unchecked")
    public Documento criar(Aluno aluno, Object... parametros) {
        if (parametros.length < 2) {
            throw new IllegalArgumentException("BoletimFactory requer: periodo (String) e notas (List<Nota>)");
        }
        String periodo = (String) parametros[0];
        List<Nota> notas = (List<Nota>) parametros[1];
        return new Boletim(aluno, periodo, notas);
    }
}
