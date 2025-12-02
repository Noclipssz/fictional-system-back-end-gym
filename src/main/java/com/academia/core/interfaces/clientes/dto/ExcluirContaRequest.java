package com.academia.core.interfaces.clientes.dto;

import jakarta.validation.constraints.NotBlank;

public class ExcluirContaRequest {

    @NotBlank(message = "Senha é obrigatória para confirmar exclusão")
    private String senha;

    public ExcluirContaRequest() {
    }

    public ExcluirContaRequest(String senha) {
        this.senha = senha;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
