package com.academia.core.interfaces.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para enviar mensagem via WebSocket
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnviarMensagemDto {
    private Long destinatarioId;
    private String conteudo;
}
