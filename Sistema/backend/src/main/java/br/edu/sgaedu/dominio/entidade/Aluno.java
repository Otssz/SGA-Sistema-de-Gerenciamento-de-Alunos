package br.edu.sgaedu.dominio.entidade;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Aluno matriculado no colégio.
 * Relacionamentos: Responsavel (ManyToMany), Matricula (OneToMany).
 */
@Entity
@Table(name = "alunos")
@DiscriminatorValue("ALUNO")
@PrimaryKeyJoinColumn(name = "usuario_id")
public class Aluno extends Usuario {

    @Column(nullable = false, unique = true, length = 20)
    private String matricula;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    /** CPF ou matrícula do responsável — protegido por LGPD. */
    @Column(length = 14)
    private String cpf;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "aluno_responsavel",
        joinColumns = @JoinColumn(name = "aluno_id"),
        inverseJoinColumns = @JoinColumn(name = "responsavel_id")
    )
    private List<Responsavel> responsaveis = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "aluno", fetch = FetchType.LAZY)
    private List<Matricula> matriculas = new ArrayList<>();

    @Override
    public String getPapel() { return "ALUNO"; }

    // --- Getters e setters ---

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public List<Responsavel> getResponsaveis() { return responsaveis; }

    public List<Matricula> getMatriculas() { return matriculas; }
}
