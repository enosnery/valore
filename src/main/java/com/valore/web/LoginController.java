package com.valore.web;

import com.valore.domain.Usuario;
import com.valore.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    private final UsuarioRepository usuarioRepository;

    public LoginController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping({"/", "/login"})
    public String login(@ModelAttribute("loginForm") LoginForm loginForm) {
        return "login";
    }

    @PostMapping("/login")
    public String autenticar(@Valid @ModelAttribute("loginForm") LoginForm loginForm,
                             BindingResult result, HttpSession session,
                             RedirectAttributes attributes) {
        if (result.hasErrors()) {
            return "login";
        }
        Usuario usuario = usuarioRepository.findByLogin(loginForm.getLogin()).orElse(null);
        if (usuario != null && usuario.getSenha().equals(loginForm.getSenha())) {
            session.setAttribute("usuarioLogado", usuario.getNome());
            session.setAttribute("usuarioId", usuario.getId());
            return "redirect:/home";
        }
        attributes.addFlashAttribute("erro", "Login ou senha inválidos.");
        return "redirect:/login";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
