package br.edu.sgaedu.dominio.entidade;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Matrícula de um Aluno em uma Turma.
 * Criada via Command Pattern (MatricularAlunoCommand) — ADR-005.
 */
@Entity
@Table(name = "matriculas",
       uniqueConstraints = @UniqueConstraint(columnNames = {"aluno_id", "turma_id"}))
public class Matricula {

    public enum Status { ATIVA, CANCELADA, TRANCADA, CONCLUIDA }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.ATIVA;

    @Column(nullable = false)
    private LocalDate dataEfetivacao;

    @Column
    private LocalDate dataCancelamento;

    @Column(length = 300)
    private String motivoCancelamento;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadaEm;

    @ManyToOne(optional = false)
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    @ManyToOne(optional = false)
    @JoinColumn(name = "turma_id")
    private Turma turma;

    @PrePersist
    private void definirDataCriacao() {
        this.criadaEm = LocalDateTime.now();
    }

    // --- Getters e setters ---

    public Long getId() { return id; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDate getDataEfetivacao() { return dataEfetivacao; }
    public void setDataEfetivacao(LocalDate dataEfetivacao) { this.dataEfetivacao = dataEfetivacao; }

    public LocalDate getDataCancelamento() { return dataCancelamento; }
    public void setDataCancelamento(LocalDate dataCancelamento) { this.dataCancelamento = dataCancelamento; }

    public String getMotivoCancelamento() { return motivoCancelamento; }
    public void setMotivoCancelamento(String motivo) { this.motivoCancelamento = motivo; }

    public LocalDateTime getCriadaEm() { return criadaEm; }

    public Aluno getAluno() { return aluno; }
    public void setAluno(Aluno aluno) { this.aluno = aluno; }

    public Turma getTurma() { return turma; }
    public void setTurma(Turma turma) { this.turma = turma; }
}
