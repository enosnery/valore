package com.valore.web;

import com.valore.domain.Fornecedor;
import com.valore.service.FornecedorService;
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
@RequestMapping("/fornecedores")
public class FornecedorController {

    private final FornecedorService service;

    public FornecedorController(FornecedorService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("fornecedores", service.listar());
        return "fornecedores/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("fornecedor", new Fornecedor());
        return "fornecedores/formulario";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("fornecedor", service.buscar(id));
        return "fornecedores/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("fornecedor") Fornecedor fornecedor,
                         BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return "fornecedores/formulario";
        }
        service.salvar(fornecedor);
        attributes.addFlashAttribute("sucesso", "Fornecedor salvo com sucesso.");
        return "redirect:/fornecedores";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes attributes) {
        try {
            service.excluir(id);
            attributes.addFlashAttribute("sucesso", "Fornecedor excluído com sucesso.");
        } catch (IllegalArgumentException exception) {
            attributes.addFlashAttribute("erro", exception.getMessage());
        }
        return "redirect:/fornecedores";
    }
}
