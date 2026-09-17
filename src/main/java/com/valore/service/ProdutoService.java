package com.valore.service;

import com.valore.domain.Produto;
import com.valore.repository.ItemTabelaPrecoRepository;
import com.valore.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository repository;
    private final ItemTabelaPrecoRepository itemTabelaPrecoRepository;

    public ProdutoService(ProdutoRepository repository, ItemTabelaPrecoRepository itemTabelaPrecoRepository) {
        this.repository = repository;
        this.itemTabelaPrecoRepository = itemTabelaPrecoRepository;
    }

    public List<Produto> listar() {
        return repository.findAll();
    }

    public Produto buscar(Long id) {
        return repository.findById(id).orElseThrow();
    }

    public Produto salvar(Produto produto) {
        return repository.save(produto);
    }

    public void excluir(Long id) {
        if (itemTabelaPrecoRepository.existsByProdutoId(id)) {
            throw new IllegalArgumentException("Não é possível excluir: existem tabelas de preço vinculadas a este produto.");
        }
        repository.deleteById(id);
    }
}
