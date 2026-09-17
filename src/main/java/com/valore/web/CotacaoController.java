package com.valore.web;

import com.valore.domain.Cotacao;
import com.valore.domain.TipoUsuario;
import com.valore.domain.Usuario;
import com.valore.repository.UsuarioRepository;
import com.valore.service.CotacaoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cotacoes")
public class CotacaoController {

    private final CotacaoService cotacaoService;
    private final UsuarioRepository usuarioRepository;

    public CotacaoController(CotacaoService cotacaoService, UsuarioRepository usuarioRepository) {
        this.cotacaoService = cotacaoService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public String listar(HttpSession session, Model model, RedirectAttributes attributes) {
        Usuario usuario = usuarioLogado(session);
        try {
            validarComprador(usuario);
            model.addAttribute("cotacoes", cotacaoService.listarDoUsuario(usuario.getId()));
            return "cotacoes/lista";
        } catch (IllegalArgumentException exception) {
            attributes.addFlashAttribute("erro", exception.getMessage());
            return "redirect:/home";
        }
    }

    @GetMapping("/nova")
    public String nova(HttpSession session, Model model, RedirectAttributes attributes) {
        Usuario usuario = usuarioLogado(session);
        if (usuario == null || usuario.getTipo() != TipoUsuario.COMPRADOR) {
            attributes.addFlashAttribute("erro", "Apenas usuários compradores podem fazer cotações.");
            return "redirect:/home";
        }
        Cotacao cotacao = cotacaoService.iniciar(usuario);
        return carregarTela(cotacao, usuario.getId(), model);
    }

    @PostMapping("/{cotacaoId}/itens/{itemTabelaPrecoId}")
    public String adicionar(@PathVariable Long cotacaoId, @PathVariable Long itemTabelaPrecoId,
                            HttpSession session, RedirectAttributes attributes) {
        Usuario usuario = usuarioLogado(session);
        try {
            validarComprador(usuario);
            cotacaoService.adicionarItem(cotacaoId, usuario.getId(), itemTabelaPrecoId);
            attributes.addFlashAttribute("sucesso", "Item adicionado à cotação.");
        } catch (IllegalArgumentException exception) {
            attributes.addFlashAttribute("erro", exception.getMessage());
        }
        return "redirect:/cotacoes/" + cotacaoId;
    }

    @PostMapping("/{cotacaoId}/salvar")
    public String salvar(@PathVariable Long cotacaoId, HttpSession session,
                         RedirectAttributes attributes) {
        Usuario usuario = usuarioLogado(session);
        try {
            validarComprador(usuario);
            cotacaoService.salvar(cotacaoId, usuario.getId());
            attributes.addFlashAttribute("sucesso", "Cotação salva com sucesso.");
            return "redirect:/cotacoes";
        } catch (IllegalArgumentException exception) {
            attributes.addFlashAttribute("erro", exception.getMessage());
            return "redirect:/cotacoes/" + cotacaoId;
        }
    }

    @PostMapping("/{cotacaoId}/excluir")
    public String excluir(@PathVariable Long cotacaoId, HttpSession session,
                          RedirectAttributes attributes) {
        Usuario usuario = usuarioLogado(session);
        try {
            validarComprador(usuario);
            cotacaoService.excluir(cotacaoId, usuario.getId());
            attributes.addFlashAttribute("sucesso", "Cotação excluída com sucesso.");
        } catch (IllegalArgumentException exception) {
            attributes.addFlashAttribute("erro", exception.getMessage());
        }
        return "redirect:/cotacoes";
    }

    @PostMapping("/{cotacaoId}/itens/{itemTabelaPrecoId}/remover")
    public String remover(@PathVariable Long cotacaoId, @PathVariable Long itemTabelaPrecoId,
                          HttpSession session, RedirectAttributes attributes) {
        Usuario usuario = usuarioLogado(session);
        try {
            validarComprador(usuario);
            cotacaoService.removerItem(cotacaoId, usuario.getId(), itemTabelaPrecoId);
            attributes.addFlashAttribute("sucesso", "Item removido da cotação.");
        } catch (IllegalArgumentException exception) {
            attributes.addFlashAttribute("erro", exception.getMessage());
        }
        return "redirect:/cotacoes/" + cotacaoId;
    }

    @GetMapping("/{cotacaoId}")
    public String visualizar(@PathVariable Long cotacaoId, HttpSession session, Model model,
                             RedirectAttributes attributes) {
        Usuario usuario = usuarioLogado(session);
        try {
            validarComprador(usuario);
            return carregarTela(cotacaoService.buscarDoUsuario(cotacaoId, usuario.getId()),
                    usuario.getId(), model);
        } catch (IllegalArgumentException exception) {
            attributes.addFlashAttribute("erro", exception.getMessage());
            return "redirect:/home";
        }
    }

    private String carregarTela(Cotacao cotacao, Long usuarioId, Model model) {
        Set<Long> selecionados = cotacao.getItens().stream()
                .map(item -> item.getItemTabelaPreco().getId())
                .collect(Collectors.toCollection(HashSet::new));
        model.addAttribute("cotacao", cotacao);
        model.addAttribute("itensDisponiveis", cotacaoService.listarItensVigentes());
        model.addAttribute("itensSelecionados", selecionados);
        return "cotacoes/formulario";
    }

    private Usuario usuarioLogado(HttpSession session) {
        Object id = session.getAttribute("usuarioId");
        if (!(id instanceof Long usuarioId)) {
            return null;
        }
        return usuarioRepository.findById(usuarioId).orElse(null);
    }

    private void validarComprador(Usuario usuario) {
        if (usuario == null || usuario.getTipo() != TipoUsuario.COMPRADOR) {
            throw new IllegalArgumentException("Apenas usuários compradores podem fazer cotações.");
        }
    }
}
