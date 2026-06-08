package br.edu.sgaedu.dominio.entidade;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Avaliação aplicada a uma Turma (prova, trabalho, etc.). */
@Entity
@Table(name = "avaliacoes")
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String descricao;

    @Column(nullable = false)
    private LocalDate dataAplicacao;

    /** Peso da avaliação — usado por MediaPonderada (Strategy). */
    @Column(nullable = false)
    private double peso;

    @Column(nullable = false)
    private double valorMaximo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "turma_id")
    private Turma turma;

    @JsonIgnore
    @OneToMany(mappedBy = "avaliacao", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Nota> notas = new ArrayList<>();

    // --- Getters e setters ---

    public Long getId() { return id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDate getDataAplicacao() { return dataAplicacao; }
    public void setDataAplicacao(LocalDate dataAplicacao) { this.dataAplicacao = dataAplicacao; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public double getValorMaximo() { return valorMaximo; }
    public void setValorMaximo(double valorMaximo) { this.valorMaximo = valorMaximo; }

    public Turma getTurma() { return turma; }
    public void setTurma(Turma turma) { this.turma = turma; }

    public List<Nota> getNotas() { return notas; }
}
