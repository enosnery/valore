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
@Table(name = "cotacao_itens",
        uniqueConstraints = @UniqueConstraint(columnNames = {"cotacao_id", "item_tabela_preco_id"}))
public class CotacaoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cotacao_id", nullable = false)
    private Cotacao cotacao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_tabela_preco_id", nullable = false)
    private ItemTabelaPreco itemTabelaPreco;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Cotacao getCotacao() { return cotacao; }
    public void setCotacao(Cotacao cotacao) { this.cotacao = cotacao; }
    public ItemTabelaPreco getItemTabelaPreco() { return itemTabelaPreco; }
    public void setItemTabelaPreco(ItemTabelaPreco itemTabelaPreco) { this.itemTabelaPreco = itemTabelaPreco; }
}
