package com.valore.web;

import jakarta.validation.constraints.NotBlank;

public class LoginForm {

    @NotBlank(message = "Informe o login")
    private String login;

    @NotBlank(message = "Informe a senha")
    private String senha;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
