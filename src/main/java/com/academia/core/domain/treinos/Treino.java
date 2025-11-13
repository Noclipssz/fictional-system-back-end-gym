package com.academia.core.domain.treinos;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "treinos")
@Getter
@Setter
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
}
