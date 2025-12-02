package com.academia.core.domain.chat;

import com.academia.core.domain.clientes.Cliente;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "chat_mensagens", indexes = {
    @Index(name = "idx_conversa_created", columnList = "conversa_id, created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Mensagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversa_id", nullable = false)
    private Conversa conversa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "remetente_id", nullable = false)
    private Cliente remetente;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String conteudo;

    @Column(nullable = false)
    private Boolean lida = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    public Mensagem(Conversa conversa, Cliente remetente, String conteudo) {
        this.conversa = conversa;
        this.remetente = remetente;
        this.conteudo = conteudo;
        this.lida = false;
    }
}
