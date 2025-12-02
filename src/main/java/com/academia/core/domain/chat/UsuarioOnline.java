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
@Table(name = "usuarios_online")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioOnline {

    @Id
    @Column(name = "cliente_id")
    private Long clienteId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", insertable = false, updatable = false)
    private Cliente cliente;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @CreationTimestamp
    @Column(name = "connected_at", updatable = false)
    private OffsetDateTime connectedAt;

    public UsuarioOnline(Cliente cliente, String sessionId) {
        this.clienteId = cliente.getId();
        // Não setamos o cliente diretamente para evitar problemas com Hibernate
        // O cliente será carregado via lazy loading se necessário
        this.sessionId = sessionId;
    }

    public UsuarioOnline(Long clienteId, String sessionId) {
        this.clienteId = clienteId;
        this.sessionId = sessionId;
    }
}
