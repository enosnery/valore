package com.valore.web;

import com.valore.domain.Produto;
import com.valore.service.ProdutoService;
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
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("produtos", service.listar());
        return "produtos/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("produto", new Produto());
        return "produtos/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("produto", service.buscar(id));
        return "produtos/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("produto") Produto produto,
                         BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return "produtos/formulario";
        }
        service.salvar(produto);
        attributes.addFlashAttribute("sucesso", "Produto salvo com sucesso.");
        return "redirect:/produtos";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes attributes) {
        try {
            service.excluir(id);
            attributes.addFlashAttribute("sucesso", "Produto excluído com sucesso.");
        } catch (IllegalArgumentException exception) {
            attributes.addFlashAttribute("erro", exception.getMessage());
        }
        return "redirect:/produtos";
    }
}
