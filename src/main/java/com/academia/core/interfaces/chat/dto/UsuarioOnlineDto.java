package com.academia.core.interfaces.chat.dto;

import com.academia.core.domain.chat.UsuarioOnline;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * DTO para usuários online
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioOnlineDto {
    private Long clienteId;
    private String nome;
    private String username;
    private String avatarDataUrl;
    private OffsetDateTime connectedAt;

    public static UsuarioOnlineDto fromEntity(UsuarioOnline usuarioOnline) {
        return new UsuarioOnlineDto(
                usuarioOnline.getCliente().getId(),
                usuarioOnline.getCliente().getNome(),
                usuarioOnline.getCliente().getUsername(),
                usuarioOnline.getCliente().getAvatarDataUrl(),
                usuarioOnline.getConnectedAt()
        );
    }
}
