package com.academia.core.domain.clientes;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "clientes")
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 150, message = "Nome deve ter entre 3 e 150 caracteres")
    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @NotBlank(message = "Username é obrigatório")
    @Size(min = 3, max = 100, message = "Username deve ter entre 3 e 100 caracteres")
    @Column(name = "username", nullable = false, unique = true, length = 100)
    private String username;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Size(max = 150, message = "Email deve ter no máximo 150 caracteres")
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    // NOVO CAMPO - Senha criptografada
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 255, message = "Senha deve ter no mínimo 6 caracteres")
    @Column(name = "senha", nullable = false, length = 255)
    private String senha;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Size(max = 30, message = "Telefone deve ter no máximo 30 caracteres")
    @Column(name = "telefone", length = 30)
    private String telefone;

    @Size(min = 11, max = 20, message = "CPF inválido")
    @Column(name = "cpf", length = 20)
    private String cpf;

    @Size(max = 255, message = "Endereço deve ter no máximo 255 caracteres")
    @Column(name = "endereco", length = 255)
    private String endereco;

    @Past(message = "Data de nascimento deve estar no passado")
    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "premium", nullable = false)
    private Boolean premium = false;

    @Column(name = "premium_ate")
    private LocalDate premiumAte;

    @Lob
    @Column(name = "avatar_data_url", columnDefinition = "LONGTEXT")
    private String avatarDataUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    // Getters and Setters
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
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

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Boolean getPremium() {
        return premium;
    }

    public void setPremium(Boolean premium) {
        this.premium = premium;
    }

    public LocalDate getPremiumAte() {
        return premiumAte;
    }

    public void setPremiumAte(LocalDate premiumAte) {
        this.premiumAte = premiumAte;
    }

    public String getAvatarDataUrl() {
        return avatarDataUrl;
    }

    public void setAvatarDataUrl(String avatarDataUrl) {
        this.avatarDataUrl = avatarDataUrl;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
