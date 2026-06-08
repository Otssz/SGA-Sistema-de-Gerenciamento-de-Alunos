package br.edu.sgaedu.dominio.entidade;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Raiz da hierarquia de usuários (herança JOINED).
 * REGRA: sem imports de Spring, Spring Security ou web nesta classe.
 */
@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "papel", discriminatorType = DiscriminatorType.STRING)
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, unique = true, length = 200)
    private String email;

    /** Armazena hash bcrypt/Argon2 — nunca a senha em texto puro (LGPD). */
    @JsonIgnore
    @Column(nullable = false)
    private String senhaHash;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    private void definirDataCriacao() {
        this.criadoEm = LocalDateTime.now();
    }

    // --- Método template: cada subtipo declara seu papel (RBAC) ---
    public abstract String getPapel();

    // --- Getters e setters ---

    public Long getId() { return id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
}
