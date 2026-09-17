package com.valore.web;

import com.valore.domain.Tela;
import com.valore.repository.TelaRepository;
import com.valore.repository.UsuarioRepository;
import com.valore.service.PermissaoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/permissoes")
public class PermissaoController {

    private final PermissaoService permissaoService;
    private final UsuarioRepository usuarioRepository;
    private final TelaRepository telaRepository;

    public PermissaoController(PermissaoService permissaoService, UsuarioRepository usuarioRepository,
                               TelaRepository telaRepository) {
        this.permissaoService = permissaoService;
        this.usuarioRepository = usuarioRepository;
        this.telaRepository = telaRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "permissoes/lista";
    }

    @GetMapping("/{usuarioId}")
    public String editar(@PathVariable Long usuarioId, Model model) {
        model.addAttribute("usuario", usuarioRepository.findById(usuarioId).orElseThrow());
        model.addAttribute("telas", telaRepository.findAll());
        List<Long> telasSelecionadas = permissaoService.telasDoUsuario(usuarioId).stream()
                .map(Tela::getId)
                .collect(Collectors.toList());
        model.addAttribute("telasSelecionadas", telasSelecionadas);
        return "permissoes/formulario";
    }

    @PostMapping("/{usuarioId}/salvar")
    public String salvar(@PathVariable Long usuarioId,
                         @RequestParam(name = "telaIds", required = false) List<Long> telaIds,
                         RedirectAttributes attributes) {
        permissaoService.substituirPermissoes(usuarioId, telaIds == null ? List.of() : telaIds);
        attributes.addFlashAttribute("sucesso", "Permissões atualizadas com sucesso.");
        return "redirect:/permissoes";
    }
}
