package br.edu.sgaedu.dominio.entidade;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/** Turma — agrega alunos de um Curso em um período letivo. */
@Entity
@Table(name = "turmas")
public class Turma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String codigo;

    @Column(nullable = false, length = 10)
    private String anoLetivo;

    /** Ex.: "Manhã", "Tarde", "Noite". */
    @Column(nullable = false, length = 20)
    private String turno;

    @ManyToOne(optional = false)
    @JoinColumn(name = "curso_id")
    private Curso curso;

    @JsonIgnore
    @ManyToMany(mappedBy = "turmas", fetch = FetchType.LAZY)
    private List<Professor> professores = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "turma", fetch = FetchType.LAZY)
    private List<Avaliacao> avaliacoes = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "turma", fetch = FetchType.LAZY)
    private List<Matricula> matriculas = new ArrayList<>();

    // --- Getters e setters ---

    public Long getId() { return id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getAnoLetivo() { return anoLetivo; }
    public void setAnoLetivo(String anoLetivo) { this.anoLetivo = anoLetivo; }

    public String getTurno() { return turno; }
    public void setTurno(String turno) { this.turno = turno; }

    public Curso getCurso() { return curso; }
    public void setCurso(Curso curso) { this.curso = curso; }

    public List<Professor> getProfessores() { return professores; }

    public List<Avaliacao> getAvaliacoes() { return avaliacoes; }

    public List<Matricula> getMatriculas() { return matriculas; }
}
