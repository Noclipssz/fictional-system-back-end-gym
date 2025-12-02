package com.academia.core.interfaces.chat.dto;

import com.academia.core.domain.chat.Mensagem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * DTO para mensagens do chat
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensagemDto {
    private Long id;
    private Long conversaId;
    private Long remetenteId;
    private String remetenteNome;
    private String remetenteUsername;
    private String conteudo;
    private boolean lida;
    private OffsetDateTime createdAt;

    public static MensagemDto fromEntity(Mensagem mensagem) {
        return new MensagemDto(
                mensagem.getId(),
                mensagem.getConversa().getId(),
                mensagem.getRemetente().getId(),
                mensagem.getRemetente().getNome(),
                mensagem.getRemetente().getUsername(),
                mensagem.getConteudo(),
                mensagem.getLida(),
                mensagem.getCreatedAt()
        );
    }
}
