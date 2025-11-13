package com.academia.core.domain.clientes;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nome completo do aluno/cliente
    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "telefone", length = 30)
    private String telefone;

    // --- Campos que estamos adicionando agora ---

    @Column(name = "cpf", length = 20)
    private String cpf;

    @Column(name = "endereco", length = 255)
    private String endereco;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "premium", nullable = false)
    private Boolean premium = false;

    @Column(name = "premium_ate")
    private LocalDate premiumAte;

    // Vamos salvar a foto já em Base64 simples por enquanto.
    // Isso não é o ideal pra produção grande, mas resolve agora sem storage S3.
    @Lob
    @Column(name = "avatar_data_url", columnDefinition = "LONGTEXT")
    private String avatarDataUrl;

    // Auditoria
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
