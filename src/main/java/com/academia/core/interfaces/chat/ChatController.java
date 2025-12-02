package com.academia.core.interfaces.chat;

import com.academia.core.application.chat.ChatService;
import com.academia.core.interfaces.chat.dto.DigitandoDto;
import com.academia.core.interfaces.chat.dto.EnviarMensagemDto;
import com.academia.core.interfaces.chat.dto.MensagemDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;

/**
 * Controller STOMP para mensagens WebSocket do chat
 */
@Controller
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Recebe mensagem privada do cliente e envia para o destinatário
     * Cliente envia para: /app/chat.enviar
     * Resposta para o remetente: /user/queue/mensagens
     */
    @MessageMapping("/chat.enviar")
    @SendToUser("/queue/mensagens")
    public MensagemDto enviarMensagem(@Payload EnviarMensagemDto payload, Principal principal) {
        log.debug("Mensagem recebida de {} para {}", principal.getName(), payload.getDestinatarioId());

        try {
            return chatService.enviarMensagem(
                    principal.getName(),
                    payload.getDestinatarioId(),
                    payload.getConteudo()
            );
        } catch (Exception e) {
            log.error("Erro ao enviar mensagem: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Notifica que o usuário está digitando
     * Cliente envia para: /app/chat.digitando
     */
    @MessageMapping("/chat.digitando")
    public void digitando(@Payload DigitandoDto payload, Principal principal) {
        log.debug("Usuário {} digitando na conversa {}", principal.getName(), payload.getConversaId());

        chatService.notificarDigitando(
                principal.getName(),
                payload.getConversaId(),
                payload.isDigitando()
        );
    }

    /**
     * Marca mensagens como lidas
     * Cliente envia para: /app/chat.lida
     */
    @MessageMapping("/chat.lida")
    public void marcarComoLida(@Payload Long conversaId, Principal principal) {
        log.debug("Marcando mensagens como lidas: conversa {}, usuário {}", conversaId, principal.getName());

        chatService.marcarComoLidas(conversaId, principal.getName());
    }
}
