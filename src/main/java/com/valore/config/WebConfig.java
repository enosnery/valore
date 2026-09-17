package com.valore.config;

import com.valore.service.PermissaoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final PermissaoService permissaoService;

    public WebConfig(PermissaoService permissaoService) {
        this.permissaoService = permissaoService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new PermissaoInterceptor(permissaoService))
                .addPathPatterns("/home", "/usuarios/**", "/cotacoes/**", "/fornecedores/**", "/produtos/**",
                        "/tabelas-preco/**", "/permissoes/**")
                .excludePathPatterns("/login");
    }

    private static class PermissaoInterceptor implements HandlerInterceptor {

        private static final Map<String, String> PREFIXO_PARA_TELA = new LinkedHashMap<>();

        static {
            PREFIXO_PARA_TELA.put("/usuarios", "USUARIOS");
            PREFIXO_PARA_TELA.put("/cotacoes", "COTACOES");
            PREFIXO_PARA_TELA.put("/fornecedores", "FORNECEDORES");
            PREFIXO_PARA_TELA.put("/produtos", "PRODUTOS");
            PREFIXO_PARA_TELA.put("/tabelas-preco", "TABELA_PRECO");
            PREFIXO_PARA_TELA.put("/permissoes", "PERMISSOES");
        }

        private final PermissaoService permissaoService;

        private PermissaoInterceptor(PermissaoService permissaoService) {
            this.permissaoService = permissaoService;
        }

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
                throws Exception {
            Object usuarioId = request.getSession().getAttribute("usuarioId");
            if (usuarioId == null) {
                response.sendRedirect("/login");
                return false;
            }
            String path = request.getRequestURI();
            String codigoTela = telaDaRota(path);
            if (codigoTela != null && !permissaoService.possuiAcesso((Long) usuarioId, codigoTela)) {
                response.sendRedirect("/home");
                return false;
            }
            return true;
        }

        private String telaDaRota(String path) {
            for (Map.Entry<String, String> entry : PREFIXO_PARA_TELA.entrySet()) {
                if (path.equals(entry.getKey()) || path.startsWith(entry.getKey() + "/")) {
                    return entry.getValue();
                }
            }
            return null;
        }
    }
}
