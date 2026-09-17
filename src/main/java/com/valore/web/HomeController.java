package com.valore.web;

import com.valore.service.PermissaoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final PermissaoService permissaoService;

    public HomeController(PermissaoService permissaoService) {
        this.permissaoService = permissaoService;
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        Object usuarioId = session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/login";
        }
        Set<String> telas = permissaoService.telasDoUsuario((Long) usuarioId).stream()
                .map(tela -> tela.getCodigo())
                .collect(Collectors.toSet());
        model.addAttribute("telas", telas);
        return "home";
    }
}
