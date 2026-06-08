package br.edu.sgaedu.dominio.entidade;

import jakarta.persistence.*;

/** Diretor — acesso total ao sistema (RBAC: COORDENADOR/DIRETOR). */
@Entity
@Table(name = "diretores")
@DiscriminatorValue("DIRETOR")
@PrimaryKeyJoinColumn(name = "usuario_id")
public class Diretor extends Usuario {

    @Column(nullable = false, length = 100)
    private String departamento;

    @Override
    public String getPapel() { return "DIRETOR"; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
}
