package com.valore.web;

import com.valore.domain.TabelaPreco;
import com.valore.domain.TipoUsuario;
import com.valore.domain.Usuario;
import com.valore.repository.ProdutoRepository;
import com.valore.repository.UsuarioRepository;
import com.valore.service.TabelaPrecoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/tabelas-preco")
public class TabelaPrecoController {

    private final TabelaPrecoService tabelaPrecoService;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;

    public TabelaPrecoController(TabelaPrecoService tabelaPrecoService, UsuarioRepository usuarioRepository,
                                 ProdutoRepository produtoRepository) {
        this.tabelaPrecoService = tabelaPrecoService;
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
    }

    @GetMapping
    public String listar(HttpSession session, Model model, RedirectAttributes attributes) {
        Usuario usuario = usuarioLogado(session);
        try {
            validarFornecedor(usuario);
            model.addAttribute("tabelas", tabelaPrecoService.listarDoFornecedor(usuario.getFornecedor().getId()));
            return "tabelas-preco/lista";
        } catch (IllegalArgumentException exception) {
            attributes.addFlashAttribute("erro", exception.getMessage());
            return "redirect:/home";
        }
    }

    @PostMapping("/nova")
    public String criar(@RequestParam String nome, HttpSession session, RedirectAttributes attributes) {
        Usuario usuario = usuarioLogado(session);
        try {
            validarFornecedor(usuario);
            TabelaPreco tabela = tabelaPrecoService.criar(usuario.getFornecedor(), nome);
            attributes.addFlashAttribute("sucesso", "Tabela de preço criada com sucesso.");
            return "redirect:/tabelas-preco/" + tabela.getId();
        } catch (IllegalArgumentException exception) {
            attributes.addFlashAttribute("erro", exception.getMessage());
            return "redirect:/tabelas-preco";
        }
    }

    @GetMapping("/{id}")
    public String visualizar(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes attributes) {
        Usuario usuario = usuarioLogado(session);
        try {
            validarFornecedor(usuario);
            TabelaPreco tabela = tabelaPrecoService.buscarDoFornecedor(id, usuario.getFornecedor().getId());
            model.addAttribute("tabela", tabela);
            model.addAttribute("produtos", produtoRepository.findAll());
            return "tabelas-preco/formulario";
        } catch (IllegalArgumentException exception) {
            attributes.addFlashAttribute("erro", exception.getMessage());
            return "redirect:/tabelas-preco";
        }
    }

    @PostMapping("/{id}/renomear")
    public String renomear(@PathVariable Long id, @RequestParam String nome, HttpSession session,
                           RedirectAttributes attributes) {
        Usuario usuario = usuarioLogado(session);
        try {
            validarFornecedor(usuario);
            tabelaPrecoService.renomear(id, usuario.getFornecedor().getId(), nome);
            attributes.addFlashAttribute("sucesso", "Tabela de preço atualizada.");
        } catch (IllegalArgumentException exception) {
            attributes.addFlashAttribute("erro", exception.getMessage());
        }
        return "redirect:/tabelas-preco/" + id;
    }

    @PostMapping("/{id}/ativar")
    public String ativar(@PathVariable Long id, HttpSession session, RedirectAttributes attributes) {
        Usuario usuario = usuarioLogado(session);
        try {
            validarFornecedor(usuario);
            tabelaPrecoService.ativar(id, usuario.getFornecedor().getId());
            attributes.addFlashAttribute("sucesso", "Tabela de preço ativada. As demais foram desativadas.");
        } catch (IllegalArgumentException exception) {
            attributes.addFlashAttribute("erro", exception.getMessage());
        }
        return "redirect:/tabelas-preco/" + id;
    }

    @PostMapping("/{id}/desativar")
    public String desativar(@PathVariable Long id, HttpSession session, RedirectAttributes attributes) {
        Usuario usuario = usuarioLogado(session);
        try {
            validarFornecedor(usuario);
            tabelaPrecoService.desativar(id, usuario.getFornecedor().getId());
            attributes.addFlashAttribute("sucesso", "Tabela de preço desativada.");
        } catch (IllegalArgumentException exception) {
            attributes.addFlashAttribute("erro", exception.getMessage());
        }
        return "redirect:/tabelas-preco/" + id;
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, HttpSession session, RedirectAttributes attributes) {
        Usuario usuario = usuarioLogado(session);
        try {
            validarFornecedor(usuario);
            tabelaPrecoService.excluir(id, usuario.getFornecedor().getId());
            attributes.addFlashAttribute("sucesso", "Tabela de preço excluída.");
        } catch (IllegalArgumentException exception) {
            attributes.addFlashAttribute("erro", exception.getMessage());
        }
        return "redirect:/tabelas-preco";
    }

    @PostMapping("/{id}/itens")
    public String adicionarItem(@PathVariable Long id, @RequestParam Long produtoId, @RequestParam BigDecimal valor,
                                @RequestParam LocalDate validadeValor, HttpSession session,
                                RedirectAttributes attributes) {
        Usuario usuario = usuarioLogado(session);
        try {
            validarFornecedor(usuario);
            tabelaPrecoService.adicionarItem(id, usuario.getFornecedor().getId(), produtoId, valor, validadeValor);
            attributes.addFlashAttribute("sucesso", "Item adicionado à tabela de preço.");
        } catch (IllegalArgumentException exception) {
            attributes.addFlashAttribute("erro", exception.getMessage());
        }
        return "redirect:/tabelas-preco/" + id;
    }

    @PostMapping("/{id}/itens/{itemId}/remover")
    public String removerItem(@PathVariable Long id, @PathVariable Long itemId, HttpSession session,
                              RedirectAttributes attributes) {
        Usuario usuario = usuarioLogado(session);
        try {
            validarFornecedor(usuario);
            tabelaPrecoService.removerItem(id, usuario.getFornecedor().getId(), itemId);
            attributes.addFlashAttribute("sucesso", "Item removido da tabela de preço.");
        } catch (IllegalArgumentException exception) {
            attributes.addFlashAttribute("erro", exception.getMessage());
        }
        return "redirect:/tabelas-preco/" + id;
    }

    private Usuario usuarioLogado(HttpSession session) {
        Object id = session.getAttribute("usuarioId");
        if (!(id instanceof Long usuarioId)) {
            return null;
        }
        return usuarioRepository.findById(usuarioId).orElse(null);
    }

    private void validarFornecedor(Usuario usuario) {
        if (usuario == null || usuario.getTipo() != TipoUsuario.FORNECEDOR || usuario.getFornecedor() == null) {
            throw new IllegalArgumentException("Apenas usuários fornecedores podem gerenciar tabelas de preço.");
        }
    }
}
