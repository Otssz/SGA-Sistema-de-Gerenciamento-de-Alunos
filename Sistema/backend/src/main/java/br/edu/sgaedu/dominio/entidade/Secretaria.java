package br.edu.sgaedu.dominio.entidade;

import jakarta.persistence.*;

/** Secretária — pode matricular alunos e gerar documentos (UC-04). */
@Entity
@Table(name = "secretarias")
@DiscriminatorValue("SECRETARIA")
@PrimaryKeyJoinColumn(name = "usuario_id")
public class Secretaria extends Usuario {

    @Column(nullable = false, length = 20)
    private String ramal;

    @Override
    public String getPapel() { return "SECRETARIA"; }

    public String getRamal() { return ramal; }
    public void setRamal(String ramal) { this.ramal = ramal; }
}
