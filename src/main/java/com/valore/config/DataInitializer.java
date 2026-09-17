package com.valore.config;

import com.valore.domain.Fornecedor;
import com.valore.domain.ItemTabelaPreco;
import com.valore.domain.PermissaoUsuario;
import com.valore.domain.Produto;
import com.valore.domain.TabelaPreco;
import com.valore.domain.Tela;
import com.valore.domain.TipoUsuario;
import com.valore.domain.Usuario;
import com.valore.repository.FornecedorRepository;
import com.valore.repository.ItemTabelaPrecoRepository;
import com.valore.repository.PermissaoUsuarioRepository;
import com.valore.repository.ProdutoRepository;
import com.valore.repository.TabelaPrecoRepository;
import com.valore.repository.TelaRepository;
import com.valore.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner criarDadosIniciais(UsuarioRepository usuarioRepository,
                                         FornecedorRepository fornecedorRepository,
                                         ProdutoRepository produtoRepository,
                                         TabelaPrecoRepository tabelaPrecoRepository,
                                         ItemTabelaPrecoRepository itemTabelaPrecoRepository,
                                         TelaRepository telaRepository,
                                         PermissaoUsuarioRepository permissaoUsuarioRepository) {
        return args -> {
            Tela telaUsuarios = criarTela(telaRepository, "USUARIOS", "Usuários");
            Tela telaCotacoes = criarTela(telaRepository, "COTACOES", "Cotações");
            Tela telaFornecedores = criarTela(telaRepository, "FORNECEDORES", "Fornecedores");
            Tela telaProdutos = criarTela(telaRepository, "PRODUTOS", "Produtos");
            Tela telaTabelaPreco = criarTela(telaRepository, "TABELA_PRECO", "Tabela de preço");
            Tela telaPermissoes = criarTela(telaRepository, "PERMISSOES", "Permissões");

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

            TabelaPreco tabelaUm = criarTabelaPrecoAtiva(fornecedorUm, "Tabela padrão Alpha", tabelaPrecoRepository);
            criarItemTabelaPreco(tabelaUm, produtoUm, new BigDecimal("125.90"), itemTabelaPrecoRepository);

            TabelaPreco tabelaDois = criarTabelaPrecoAtiva(fornecedorDois, "Tabela padrão Beta", tabelaPrecoRepository);
            criarItemTabelaPreco(tabelaDois, produtoDois, new BigDecimal("119.50"), itemTabelaPrecoRepository);

            Usuario admin = criarUsuario(usuarioRepository, "admin", "admin", "Administrador",
                    TipoUsuario.COMPRADOR, null);
            Usuario comprador1 = criarUsuario(usuarioRepository, "comprador1", "comprador1", "Comprador Um",
                    TipoUsuario.COMPRADOR, null);
            Usuario fornecedor1 = criarUsuario(usuarioRepository, "fornecedor1", "fornecedor1", "Fornecedor Um",
                    TipoUsuario.FORNECEDOR, fornecedorUm);

            concederPermissao(permissaoUsuarioRepository, admin, telaUsuarios);
            concederPermissao(permissaoUsuarioRepository, admin, telaPermissoes);
            concederPermissao(permissaoUsuarioRepository, admin, telaFornecedores);
            concederPermissao(permissaoUsuarioRepository, admin, telaProdutos);
            concederPermissao(permissaoUsuarioRepository, comprador1, telaCotacoes);
            concederPermissao(permissaoUsuarioRepository, fornecedor1, telaTabelaPreco);
        };
    }

    private Tela criarTela(TelaRepository repository, String codigo, String nome) {
        return repository.findByCodigo(codigo).orElseGet(() -> repository.save(new Tela(codigo, nome)));
    }

    private Usuario criarUsuario(UsuarioRepository repository, String login, String senha, String nome,
                                 TipoUsuario tipo, Fornecedor fornecedor) {
        return repository.findByLogin(login).orElseGet(() -> {
            Usuario usuario = new Usuario();
            usuario.setNome(nome);
            usuario.setLogin(login);
            usuario.setSenha(senha);
            usuario.setTipo(tipo);
            usuario.setFornecedor(fornecedor);
            return repository.save(usuario);
        });
    }

    private void concederPermissao(PermissaoUsuarioRepository repository, Usuario usuario, Tela tela) {
        if (!repository.existsByUsuarioIdAndTelaCodigo(usuario.getId(), tela.getCodigo())) {
            repository.save(new PermissaoUsuario(usuario, tela));
        }
    }

    private TabelaPreco criarTabelaPrecoAtiva(Fornecedor fornecedor, String nome, TabelaPrecoRepository repository) {
        List<TabelaPreco> existentes = repository.findByFornecedorIdOrderByNomeAsc(fornecedor.getId());
        for (TabelaPreco tabela : existentes) {
            if (tabela.getNome().equals(nome)) {
                return tabela;
            }
        }
        TabelaPreco tabelaPreco = new TabelaPreco();
        tabelaPreco.setFornecedor(fornecedor);
        tabelaPreco.setNome(nome);
        tabelaPreco.setAtiva(true);
        return repository.save(tabelaPreco);
    }

    private void criarItemTabelaPreco(TabelaPreco tabelaPreco, Produto produto, BigDecimal valor,
                                      ItemTabelaPrecoRepository repository) {
        if (!repository.existsByTabelaPrecoIdAndProdutoId(tabelaPreco.getId(), produto.getId())) {
            ItemTabelaPreco item = new ItemTabelaPreco();
            item.setTabelaPreco(tabelaPreco);
            item.setProduto(produto);
            item.setValor(valor);
            item.setValidadeValor(LocalDate.now().plusDays(30));
            repository.save(item);
            }
    }
}
