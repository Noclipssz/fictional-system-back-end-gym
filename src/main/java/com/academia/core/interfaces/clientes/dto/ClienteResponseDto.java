package com.academia.core.interfaces.clientes.dto;

public class ClienteResponseDto {

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String cpf;
    private String endereco;
    private String dataNascimento; // yyyy-MM-dd
    private Boolean premium;
    private String premiumAte; // yyyy-MM-dd
    private String avatarDataUrl;

    public ClienteResponseDto() {
    }

    public ClienteResponseDto(Long id, String nome, String email, String telefone, String cpf, String endereco,
                              String dataNascimento, Boolean premium, String premiumAte, String avatarDataUrl) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.cpf = cpf;
        this.endereco = endereco;
        this.dataNascimento = dataNascimento;
        this.premium = premium;
        this.premiumAte = premiumAte;
        this.avatarDataUrl = avatarDataUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Boolean getPremium() {
        return premium;
    }

    public void setPremium(Boolean premium) {
        this.premium = premium;
    }

    public String getPremiumAte() {
        return premiumAte;
    }

    public void setPremiumAte(String premiumAte) {
        this.premiumAte = premiumAte;
    }

    public String getAvatarDataUrl() {
        return avatarDataUrl;
    }

    public void setAvatarDataUrl(String avatarDataUrl) {
        this.avatarDataUrl = avatarDataUrl;
    }
}
