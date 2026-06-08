package br.edu.sgaedu.dominio.documento;

import br.edu.sgaedu.dominio.entidade.Aluno;
import br.edu.sgaedu.dominio.entidade.Avaliacao;
import br.edu.sgaedu.dominio.entidade.Nota;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Produto concreto do Factory Method para boletim periodico.
 */
public class Boletim extends Documento {

    private final String periodo;
    private final List<Nota> notas;

    public Boletim(Aluno aluno, String periodo, List<Nota> notas) {
        super(aluno);
        this.periodo = periodo;
        this.notas = notas;
    }

    @Override
    public byte[] gerarPdf() {
        try {
            ByteArrayOutputStream saida = new ByteArrayOutputStream();
            com.itextpdf.text.Document pdf = new com.itextpdf.text.Document();
            PdfWriter.getInstance(pdf, saida);

            pdf.open();
            pdf.add(titulo("Boletim Escolar"));
            pdf.add(paragrafo("Aluno: " + valor(aluno.getNome())));
            pdf.add(paragrafo("Matricula: " + valor(aluno.getMatricula())));
            pdf.add(paragrafo("Periodo: " + valor(periodo)));
            pdf.add(paragrafo("Gerado em: " + geradoEm.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
            pdf.add(new Paragraph(" "));

            PdfPTable tabela = new PdfPTable(new float[] { 3f, 1f, 1f, 1f, 2f });
            tabela.setWidthPercentage(100);
            adicionarCabecalho(tabela, "Avaliacao", "Nota", "Peso", "Maximo", "Data");

            if (notas == null || notas.isEmpty()) {
                PdfPCell celula = new PdfPCell(new Phrase("Nenhuma nota lancada para o periodo."));
                celula.setColspan(5);
                celula.setPadding(8);
                tabela.addCell(celula);
            } else {
                for (Nota nota : notas) {
                    Avaliacao avaliacao = nota.getAvaliacao();
                    tabela.addCell(celula(valor(avaliacao != null ? avaliacao.getDescricao() : null)));
                    tabela.addCell(celula(numero(nota.getValor())));
                    tabela.addCell(celula(numero(avaliacao != null ? avaliacao.getPeso() : 0)));
                    tabela.addCell(celula(numero(avaliacao != null ? avaliacao.getValorMaximo() : 0)));
                    tabela.addCell(celula(avaliacao != null && avaliacao.getDataAplicacao() != null
                            ? avaliacao.getDataAplicacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            : "-"));
                }
            }

            pdf.add(tabela);
            pdf.add(new Paragraph(" "));
            pdf.add(paragrafo("Documento gerado pelo SGA-Edu conforme RF-050 / UC-04."));
            pdf.close();
            return saida.toByteArray();
        } catch (DocumentException ex) {
            throw new IllegalStateException("Nao foi possivel gerar o boletim em PDF.", ex);
        }
    }

    @Override
    public String getTipo() { return "BOLETIM"; }

    public String getPeriodo() { return periodo; }

    public List<Nota> getNotas() { return notas; }

    private Paragraph titulo(String texto) {
        Paragraph titulo = new Paragraph(texto, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(12);
        return titulo;
    }

    private Paragraph paragrafo(String texto) {
        return new Paragraph(texto, FontFactory.getFont(FontFactory.HELVETICA, 11));
    }

    private void adicionarCabecalho(PdfPTable tabela, String... colunas) {
        Font fonte = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        for (String coluna : colunas) {
            PdfPCell celula = new PdfPCell(new Phrase(coluna, fonte));
            celula.setHorizontalAlignment(Element.ALIGN_CENTER);
            celula.setPadding(6);
            tabela.addCell(celula);
        }
    }

    private PdfPCell celula(String texto) {
        PdfPCell celula = new PdfPCell(new Phrase(valor(texto), FontFactory.getFont(FontFactory.HELVETICA, 10)));
        celula.setPadding(6);
        return celula;
    }

    private String numero(double valor) {
        return String.format(Locale.US, "%.2f", valor);
    }

    private String valor(String texto) {
        return texto == null || texto.isBlank() ? "-" : texto;
    }
}
