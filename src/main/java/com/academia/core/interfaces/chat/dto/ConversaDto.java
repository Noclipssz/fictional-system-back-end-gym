package com.academia.core.interfaces.chat.dto;

import com.academia.core.domain.chat.Conversa;
import com.academia.core.domain.clientes.Cliente;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * DTO para conversas do chat
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversaDto {
    private Long id;
    private Long outroUsuarioId;
    private String outroUsuarioNome;
    private String outroUsuarioUsername;
    private String outroUsuarioAvatar;
    private boolean outroUsuarioOnline;
    private String ultimaMensagem;
    private OffsetDateTime ultimaMensagemAt;
    private Long mensagensNaoLidas;

    public static ConversaDto fromEntity(Conversa conversa, Long meuUsuarioId,
                                          String ultimaMensagem, Long mensagensNaoLidas,
                                          boolean outroUsuarioOnline) {
        Cliente outroUsuario = conversa.getOutroUsuario(meuUsuarioId);

        return new ConversaDto(
                conversa.getId(),
                outroUsuario.getId(),
                outroUsuario.getNome(),
                outroUsuario.getUsername(),
                outroUsuario.getAvatarDataUrl(),
                outroUsuarioOnline,
                ultimaMensagem,
                conversa.getUltimaMensagemAt(),
                mensagensNaoLidas
        );
    }
}
