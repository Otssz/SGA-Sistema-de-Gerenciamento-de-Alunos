package br.edu.sgaedu.dominio.entidade;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Registro de presença/falta de um Aluno em uma aula da Turma (UC-05). */
@Entity
@Table(name = "frequencias",
       uniqueConstraints = @UniqueConstraint(columnNames = {"aluno_id", "turma_id", "data_aula"}))
public class Frequencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_aula", nullable = false)
    private LocalDate dataAula;

    @Column(nullable = false)
    private boolean presente;

    /** Justificativa opcional para ausência. */
    @Column(length = 500)
    private String justificativa;

    @Column(nullable = false, updatable = false)
    private LocalDateTime registradoEm;

    @ManyToOne(optional = false)
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    @ManyToOne(optional = false)
    @JoinColumn(name = "turma_id")
    private Turma turma;

    /** Professor que registrou — rastreabilidade LGPD. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "professor_id")
    private Professor registradoPor;

    @PrePersist
    private void definirDataRegistro() {
        this.registradoEm = LocalDateTime.now();
    }

    // --- Getters e setters ---

    public Long getId() { return id; }

    public LocalDate getDataAula() { return dataAula; }
    public void setDataAula(LocalDate dataAula) { this.dataAula = dataAula; }

    public boolean isPresente() { return presente; }
    public void setPresente(boolean presente) { this.presente = presente; }

    public String getJustificativa() { return justificativa; }
    public void setJustificativa(String justificativa) { this.justificativa = justificativa; }

    public LocalDateTime getRegistradoEm() { return registradoEm; }

    public Aluno getAluno() { return aluno; }
    public void setAluno(Aluno aluno) { this.aluno = aluno; }

    public Turma getTurma() { return turma; }
    public void setTurma(Turma turma) { this.turma = turma; }

    public Professor getRegistradoPor() { return registradoPor; }
    public void setRegistradoPor(Professor professor) { this.registradoPor = professor; }
}
