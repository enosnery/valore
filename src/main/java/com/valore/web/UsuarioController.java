package com.valore.web;

import com.valore.domain.Usuario;
import com.valore.domain.TipoUsuario;
import com.valore.repository.FornecedorRepository;
import com.valore.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final FornecedorRepository fornecedorRepository;

    public UsuarioController(UsuarioService usuarioService, FornecedorRepository fornecedorRepository) {
        this.usuarioService = usuarioService;
        this.fornecedorRepository = fornecedorRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listar());
        return "usuarios/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("tiposUsuario", TipoUsuario.values());
        model.addAttribute("fornecedores", fornecedorRepository.findAll());
        return "usuarios/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", usuarioService.buscar(id));
        model.addAttribute("tiposUsuario", TipoUsuario.values());
        model.addAttribute("fornecedores", fornecedorRepository.findAll());
        return "usuarios/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("usuario") Usuario usuario,
                         BindingResult result, Model model,
                         RedirectAttributes attributes) {
        if (result.hasErrors()) {
            model.addAttribute("tiposUsuario", TipoUsuario.values());
            model.addAttribute("fornecedores", fornecedorRepository.findAll());
            return "usuarios/formulario";
        }
        if (usuario.getTipo() == TipoUsuario.FORNECEDOR && usuario.getFornecedor() == null) {
            result.rejectValue("fornecedor", "required", "O fornecedor é obrigatório para este tipo de usuário.");
            model.addAttribute("tiposUsuario", TipoUsuario.values());
            model.addAttribute("fornecedores", fornecedorRepository.findAll());
            return "usuarios/formulario";
        }
        if (usuario.getTipo() == TipoUsuario.COMPRADOR) {
            usuario.setFornecedor(null);
        }
        usuarioService.salvar(usuario);
        attributes.addFlashAttribute("sucesso", "Usuário salvo com sucesso.");
        return "redirect:/usuarios";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes attributes) {
        usuarioService.excluir(id);
        attributes.addFlashAttribute("sucesso", "Usuário excluído com sucesso.");
        return "redirect:/usuarios";
    }
}
