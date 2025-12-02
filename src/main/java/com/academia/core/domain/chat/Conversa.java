package com.academia.core.domain.chat;

import com.academia.core.domain.clientes.Cliente;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "chat_conversas", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"usuario1_id", "usuario2_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Conversa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario1_id", nullable = false)
    private Cliente usuario1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario2_id", nullable = false)
    private Cliente usuario2;

    @Column(name = "ultima_mensagem_at")
    private OffsetDateTime ultimaMensagemAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    public Conversa(Cliente usuario1, Cliente usuario2) {
        this.usuario1 = usuario1;
        this.usuario2 = usuario2;
        this.ultimaMensagemAt = OffsetDateTime.now();
    }

    /**
     * Verifica se um usuário faz parte desta conversa
     */
    public boolean contemUsuario(Long usuarioId) {
        return usuario1.getId().equals(usuarioId) || usuario2.getId().equals(usuarioId);
    }

    /**
     * Retorna o outro usuário da conversa
     */
    public Cliente getOutroUsuario(Long usuarioId) {
        return usuario1.getId().equals(usuarioId) ? usuario2 : usuario1;
    }
}
