package br.edu.sgaedu.dominio.entidade;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Nota de um Aluno em uma Avaliação. Lançada pelo Professor (UC-02). */
@Entity
@Table(name = "notas",
       uniqueConstraints = @UniqueConstraint(columnNames = {"aluno_id", "avaliacao_id"}))
public class Nota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double valor;

    @Column(length = 500)
    private String observacao;

    @Column(nullable = false, updatable = false)
    private LocalDateTime lancadaEm;

    @ManyToOne(optional = false)
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    @ManyToOne(optional = false)
    @JoinColumn(name = "avaliacao_id")
    private Avaliacao avaliacao;

    /** Professor que registrou a nota — rastreabilidade LGPD. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "professor_id")
    private Professor lancadaPor;

    @PrePersist
    private void definirDataLancamento() {
        this.lancadaEm = LocalDateTime.now();
    }

    // --- Getters e setters ---

    public Long getId() { return id; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public LocalDateTime getLancadaEm() { return lancadaEm; }

    public Aluno getAluno() { return aluno; }
    public void setAluno(Aluno aluno) { this.aluno = aluno; }

    public Avaliacao getAvaliacao() { return avaliacao; }
    public void setAvaliacao(Avaliacao avaliacao) { this.avaliacao = avaliacao; }

    public Professor getLancadaPor() { return lancadaPor; }
    public void setLancadaPor(Professor professor) { this.lancadaPor = professor; }
}
