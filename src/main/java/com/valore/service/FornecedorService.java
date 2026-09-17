package com.valore.service;

import com.valore.domain.Fornecedor;
import com.valore.repository.FornecedorRepository;
import com.valore.repository.TabelaPrecoRepository;
import com.valore.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FornecedorService {

    private final FornecedorRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final TabelaPrecoRepository tabelaPrecoRepository;

    public FornecedorService(FornecedorRepository repository, UsuarioRepository usuarioRepository,
                             TabelaPrecoRepository tabelaPrecoRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.tabelaPrecoRepository = tabelaPrecoRepository;
    }

    public List<Fornecedor> listar() {
        return repository.findAll();
    }

    public Fornecedor buscar(Long id) {
        return repository.findById(id).orElseThrow();
    }

    public Fornecedor salvar(Fornecedor fornecedor) {
        return repository.save(fornecedor);
    }

    public void excluir(Long id) {
        if (usuarioRepository.existsByFornecedorId(id)) {
            throw new IllegalArgumentException("Não é possível excluir: existem usuários vinculados a este fornecedor.");
        }
        if (tabelaPrecoRepository.existsByFornecedorId(id)) {
            throw new IllegalArgumentException("Não é possível excluir: existem tabelas de preço vinculadas a este fornecedor.");
        }
        repository.deleteById(id);
    }
}
