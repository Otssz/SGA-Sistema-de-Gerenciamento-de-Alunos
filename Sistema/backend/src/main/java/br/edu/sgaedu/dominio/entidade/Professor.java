package br.edu.sgaedu.dominio.entidade;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/** Professor que lança notas e frequências (UC-02). */
@Entity
@Table(name = "professores")
@DiscriminatorValue("PROFESSOR")
@PrimaryKeyJoinColumn(name = "usuario_id")
public class Professor extends Usuario {

    @Column(nullable = false, length = 100)
    private String especialidade;

    @Column(nullable = false, unique = true, length = 20)
    private String registro;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "professor_turma",
        joinColumns = @JoinColumn(name = "professor_id"),
        inverseJoinColumns = @JoinColumn(name = "turma_id")
    )
    private List<Turma> turmas = new ArrayList<>();

    @Override
    public String getPapel() { return "PROFESSOR"; }

    // --- Getters e setters ---

    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }

    public String getRegistro() { return registro; }
    public void setRegistro(String registro) { this.registro = registro; }

    public List<Turma> getTurmas() { return turmas; }
}
