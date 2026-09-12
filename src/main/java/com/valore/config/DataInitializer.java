package com.valore.config;

import com.valore.domain.Usuario;
import com.valore.domain.TipoUsuario;
import com.valore.domain.Fornecedor;
import com.valore.domain.Produto;
import com.valore.domain.TabelaPreco;
import com.valore.repository.FornecedorRepository;
import com.valore.repository.ProdutoRepository;
import com.valore.repository.TabelaPrecoRepository;
import com.valore.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner criarDadosIniciais(UsuarioRepository usuarioRepository,
                                         FornecedorRepository fornecedorRepository,
                                         ProdutoRepository produtoRepository,
                                         TabelaPrecoRepository tabelaPrecoRepository) {
        return args -> {
            if (usuarioRepository.count() == 0) {
                Usuario usuario = new Usuario();
                usuario.setNome("Administrador");
                usuario.setLogin("admin");
                usuario.setSenha("admin");
                usuario.setTipo(TipoUsuario.COMPRADOR);
                usuarioRepository.save(usuario);
            }

            Fornecedor fornecedorUm = fornecedorRepository.findByCnpj("11.111.111/0001-11")
                    .orElseGet(() -> {
                        Fornecedor fornecedor = new Fornecedor();
                        fornecedor.setRazaoSocial("Fornecedor Alpha Ltda.");
                        fornecedor.setCnpj("11.111.111/0001-11");
                        return fornecedorRepository.save(fornecedor);
                    });
            Fornecedor fornecedorDois = fornecedorRepository.findByCnpj("22.222.222/0001-22")
                    .orElseGet(() -> {
                        Fornecedor fornecedor = new Fornecedor();
                        fornecedor.setRazaoSocial("Fornecedor Beta S.A.");
                        fornecedor.setCnpj("22.222.222/0001-22");
                        return fornecedorRepository.save(fornecedor);
                    });

            Produto produtoUm = produtoRepository.findByNome("Produto Alpha")
                    .orElseGet(() -> {
                        Produto produto = new Produto();
                        produto.setNome("Produto Alpha");
                        produto.setSku("SKU-TESTE-001");
                        return produtoRepository.save(produto);
                    });
            Produto produtoDois = produtoRepository.findByNome("Produto Beta")
                    .orElseGet(() -> {
                        Produto produto = new Produto();
                        produto.setNome("Produto Beta");
                        produto.setSku("SKU-TESTE-001");
                        return produtoRepository.save(produto);
                    });

            criarTabelaPreco(fornecedorUm, produtoUm, new BigDecimal("125.90"), tabelaPrecoRepository);
            criarTabelaPreco(fornecedorDois, produtoDois, new BigDecimal("119.50"), tabelaPrecoRepository);
        };
    }

    private void criarTabelaPreco(Fornecedor fornecedor, Produto produto, BigDecimal valor,
                                  TabelaPrecoRepository repository) {
        if (!repository.existsByFornecedorIdAndProdutoId(fornecedor.getId(), produto.getId())) {
            TabelaPreco tabelaPreco = new TabelaPreco();
            tabelaPreco.setFornecedor(fornecedor);
            tabelaPreco.setProduto(produto);
            tabelaPreco.setValor(valor);
            tabelaPreco.setValidadeValor(LocalDate.now().plusDays(30));
            repository.save(tabelaPreco);
        }
    }
}
