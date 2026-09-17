package com.valore.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "itens_tabela_preco",
        uniqueConstraints = @UniqueConstraint(columnNames = {"tabela_preco_id", "produto_id"}))
public class ItemTabelaPreco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "A tabela de preço é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tabela_preco_id", nullable = false)
    private TabelaPreco tabelaPreco;

    @NotNull(message = "O produto é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @NotNull(message = "O valor é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "O valor deve ser maior que zero")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @NotNull(message = "A validade do valor é obrigatória")
    @Column(name = "validade_valor", nullable = false)
    private LocalDate validadeValor;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public TabelaPreco getTabelaPreco() { return tabelaPreco; }
    public void setTabelaPreco(TabelaPreco tabelaPreco) { this.tabelaPreco = tabelaPreco; }
    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public LocalDate getValidadeValor() { return validadeValor; }
    public void setValidadeValor(LocalDate validadeValor) { this.validadeValor = validadeValor; }
}
