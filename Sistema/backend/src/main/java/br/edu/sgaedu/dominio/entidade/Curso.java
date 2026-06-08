package br.edu.sgaedu.dominio.entidade;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/** Curso ofertado pelo colégio (ex.: Ensino Médio 1º Ano). */
@Entity
@Table(name = "cursos")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 500)
    private String descricao;

    /** Carga horária total em horas. */
    @Column(nullable = false)
    private int cargaHorariaTotal;

    @JsonIgnore
    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Turma> turmas = new ArrayList<>();

    // --- Getters e setters ---

    public Long getId() { return id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public int getCargaHorariaTotal() { return cargaHorariaTotal; }
    public void setCargaHorariaTotal(int cargaHorariaTotal) { this.cargaHorariaTotal = cargaHorariaTotal; }

    public List<Turma> getTurmas() { return turmas; }
}
