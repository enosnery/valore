package com.valore.service;

import com.valore.domain.Fornecedor;
import com.valore.domain.ItemTabelaPreco;
import com.valore.domain.Produto;
import com.valore.domain.TabelaPreco;
import com.valore.repository.CotacaoItemRepository;
import com.valore.repository.ItemTabelaPrecoRepository;
import com.valore.repository.ProdutoRepository;
import com.valore.repository.TabelaPrecoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TabelaPrecoService {

    private final TabelaPrecoRepository repository;
    private final ItemTabelaPrecoRepository itemRepository;
    private final ProdutoRepository produtoRepository;
    private final CotacaoItemRepository cotacaoItemRepository;

    public TabelaPrecoService(TabelaPrecoRepository repository, ItemTabelaPrecoRepository itemRepository,
                              ProdutoRepository produtoRepository, CotacaoItemRepository cotacaoItemRepository) {
        this.repository = repository;
        this.itemRepository = itemRepository;
        this.produtoRepository = produtoRepository;
        this.cotacaoItemRepository = cotacaoItemRepository;
    }

    @Transactional(readOnly = true)
    public List<TabelaPreco> listarDoFornecedor(Long fornecedorId) {
        return repository.findByFornecedorIdOrderByNomeAsc(fornecedorId);
    }

    @Transactional(readOnly = true)
    public TabelaPreco buscarDoFornecedor(Long id, Long fornecedorId) {
        return repository.findByIdAndFornecedorId(id, fornecedorId)
                .orElseThrow(() -> new IllegalArgumentException("Tabela de preço não encontrada."));
    }

    @Transactional
    public TabelaPreco criar(Fornecedor fornecedor, String nome) {
        TabelaPreco tabelaPreco = new TabelaPreco();
        tabelaPreco.setFornecedor(fornecedor);
        tabelaPreco.setNome(nome);
        tabelaPreco.setAtiva(false);
        return repository.save(tabelaPreco);
    }

    @Transactional
    public void renomear(Long id, Long fornecedorId, String nome) {
        TabelaPreco tabelaPreco = buscarDoFornecedor(id, fornecedorId);
        tabelaPreco.setNome(nome);
        repository.save(tabelaPreco);
    }

    @Transactional
    public void ativar(Long id, Long fornecedorId) {
        TabelaPreco tabelaPreco = buscarDoFornecedor(id, fornecedorId);
        List<TabelaPreco> outrasAtivas = repository.findByFornecedorIdAndAtivaTrueAndIdNot(fornecedorId, id);
        for (TabelaPreco outra : outrasAtivas) {
            outra.setAtiva(false);
            repository.save(outra);
        }
        tabelaPreco.setAtiva(true);
        repository.save(tabelaPreco);
    }

    @Transactional
    public void desativar(Long id, Long fornecedorId) {
        TabelaPreco tabelaPreco = buscarDoFornecedor(id, fornecedorId);
        tabelaPreco.setAtiva(false);
        repository.save(tabelaPreco);
    }

    @Transactional
    public void excluir(Long id, Long fornecedorId) {
        TabelaPreco tabelaPreco = buscarDoFornecedor(id, fornecedorId);
        if (cotacaoItemRepository.existsByItemTabelaPrecoTabelaPrecoId(id)) {
            throw new IllegalArgumentException("Não é possível excluir: existem cotações com itens desta tabela de preço.");
        }
        repository.delete(tabelaPreco);
    }

    @Transactional
    public void adicionarItem(Long tabelaPrecoId, Long fornecedorId, Long produtoId, BigDecimal valor,
                              LocalDate validadeValor) {
        TabelaPreco tabelaPreco = buscarDoFornecedor(tabelaPrecoId, fornecedorId);
        if (itemRepository.existsByTabelaPrecoIdAndProdutoId(tabelaPrecoId, produtoId)) {
            throw new IllegalArgumentException("Este produto já está nesta tabela de preço.");
        }
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
        ItemTabelaPreco item = new ItemTabelaPreco();
        item.setTabelaPreco(tabelaPreco);
        item.setProduto(produto);
        item.setValor(valor);
        item.setValidadeValor(validadeValor);
        itemRepository.save(item);
    }

    @Transactional
    public void removerItem(Long tabelaPrecoId, Long fornecedorId, Long itemId) {
        buscarDoFornecedor(tabelaPrecoId, fornecedorId);
        ItemTabelaPreco item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado."));
        if (!item.getTabelaPreco().getId().equals(tabelaPrecoId)) {
            throw new IllegalArgumentException("Item não pertence a esta tabela de preço.");
        }
        if (cotacaoItemRepository.existsByItemTabelaPrecoId(itemId)) {
            throw new IllegalArgumentException("Não é possível remover: este item está sendo usado em uma cotação.");
        }
        itemRepository.delete(item);
    }
}
