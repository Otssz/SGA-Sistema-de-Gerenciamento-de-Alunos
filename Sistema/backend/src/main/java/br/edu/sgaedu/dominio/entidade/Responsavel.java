package br.edu.sgaedu.dominio.entidade;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/** Responsável legal pelo aluno — pode receber notificações (Observer). */
@Entity
@Table(name = "responsaveis")
public class Responsavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, unique = true, length = 200)
    private String email;

    @Column(length = 20)
    private String telefone;

    /** Grau de parentesco (ex.: "Mãe", "Pai", "Tutor"). */
    @Column(nullable = false, length = 50)
    private String parentesco;

    @ManyToMany(mappedBy = "responsaveis", fetch = FetchType.LAZY)
    private List<Aluno> alunos = new ArrayList<>();

    // --- Getters e setters ---

    public Long getId() { return id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getParentesco() { return parentesco; }
    public void setParentesco(String parentesco) { this.parentesco = parentesco; }

    public List<Aluno> getAlunos() { return alunos; }
}
