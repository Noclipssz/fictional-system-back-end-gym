package com.academia.core.domain.treinos;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "treinos")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Treino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    private String titulo;

    private String descricao;

    @Enumerated(EnumType.STRING)
    private NivelTreino nivel;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    // Setters needed by service layer
    public void setId(Long id) {
        this.id = id;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setNivel(NivelTreino nivel) {
        this.nivel = nivel;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
