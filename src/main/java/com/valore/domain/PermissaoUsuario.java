package com.valore.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "permissoes_usuario",
        uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "tela_id"}))
public class PermissaoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tela_id", nullable = false)
    private Tela tela;

    public PermissaoUsuario() {
    }

    public PermissaoUsuario(Usuario usuario, Tela tela) {
        this.usuario = usuario;
        this.tela = tela;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Tela getTela() { return tela; }
    public void setTela(Tela tela) { this.tela = tela; }
}
