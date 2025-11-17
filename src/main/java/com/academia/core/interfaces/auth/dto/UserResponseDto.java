package com.academia.core.interfaces.auth.dto;

import java.util.Set;

public class UserResponseDto {

    private Long id;
    private String username;
    private String email;
    private String nome;
    private Boolean active;
    private Set<String> roles;

    public UserResponseDto() {
    }

    public UserResponseDto(Long id, String username, String email, String nome, Boolean active, Set<String> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.nome = nome;
        this.active = active;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
