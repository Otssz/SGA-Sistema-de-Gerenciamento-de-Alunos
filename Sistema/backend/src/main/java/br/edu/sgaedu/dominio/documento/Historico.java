package br.edu.sgaedu.dominio.documento;

import br.edu.sgaedu.dominio.entidade.Aluno;
import br.edu.sgaedu.dominio.entidade.Matricula;
import br.edu.sgaedu.dominio.entidade.Turma;
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

/**
 * Produto concreto do Factory Method para historico escolar.
 */
public class Historico extends Documento {

    private final List<Matricula> historicoDeCursos;

    public Historico(Aluno aluno, List<Matricula> historicoDeCursos) {
        super(aluno);
        this.historicoDeCursos = historicoDeCursos;
    }

    @Override
    public byte[] gerarPdf() {
        try {
            ByteArrayOutputStream saida = new ByteArrayOutputStream();
            com.itextpdf.text.Document pdf = new com.itextpdf.text.Document();
            PdfWriter.getInstance(pdf, saida);

            pdf.open();
            pdf.add(titulo("Historico Escolar"));
            pdf.add(paragrafo("Aluno: " + valor(aluno.getNome())));
            pdf.add(paragrafo("Matricula: " + valor(aluno.getMatricula())));
            pdf.add(paragrafo("Data de nascimento: " + (aluno.getDataNascimento() == null
                    ? "-"
                    : aluno.getDataNascimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))));
            pdf.add(paragrafo("Gerado em: " + geradoEm.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
            pdf.add(new Paragraph(" "));

            PdfPTable tabela = new PdfPTable(new float[] { 2f, 3f, 2f, 2f, 2f, 3f });
            tabela.setWidthPercentage(100);
            adicionarCabecalho(tabela, "Turma", "Curso", "Ano", "Status", "Efetivacao", "Cancelamento/Motivo");

            if (historicoDeCursos == null || historicoDeCursos.isEmpty()) {
                PdfPCell celula = new PdfPCell(new Phrase("Nenhuma matricula encontrada para o aluno."));
                celula.setColspan(6);
                celula.setPadding(8);
                tabela.addCell(celula);
            } else {
                for (Matricula matricula : historicoDeCursos) {
                    Turma turma = matricula.getTurma();
                    tabela.addCell(celula(valor(turma != null ? turma.getCodigo() : null)));
                    tabela.addCell(celula(turma != null && turma.getCurso() != null ? turma.getCurso().getNome() : "-"));
                    tabela.addCell(celula(valor(turma != null ? turma.getAnoLetivo() : null)));
                    tabela.addCell(celula(matricula.getStatus() == null ? "-" : matricula.getStatus().name()));
                    tabela.addCell(celula(matricula.getDataEfetivacao() == null
                            ? "-"
                            : matricula.getDataEfetivacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
                    tabela.addCell(celula(cancelamento(matricula)));
                }
            }

            pdf.add(tabela);
            pdf.add(new Paragraph(" "));
            pdf.add(paragrafo("Documento gerado pelo SGA-Edu conforme RF-050 / UC-04."));
            pdf.close();
            return saida.toByteArray();
        } catch (DocumentException ex) {
            throw new IllegalStateException("Nao foi possivel gerar o historico em PDF.", ex);
        }
    }

    @Override
    public String getTipo() { return "HISTORICO"; }

    public List<Matricula> getHistoricoDeCursos() { return historicoDeCursos; }

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

    private String cancelamento(Matricula matricula) {
        if (matricula.getDataCancelamento() == null && matricula.getMotivoCancelamento() == null) {
            return "-";
        }
        String data = matricula.getDataCancelamento() == null
                ? "-"
                : matricula.getDataCancelamento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        return data + " / " + valor(matricula.getMotivoCancelamento());
    }

    private String valor(String texto) {
        return texto == null || texto.isBlank() ? "-" : texto;
    }
}
