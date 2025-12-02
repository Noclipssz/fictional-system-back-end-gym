package com.academia.core.config.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

/**
 * Interceptor para processar frames STOMP.
 * Configura o Principal para mensagens WebSocket.
 */
@Component
public class JwtChannelInterceptor implements ChannelInterceptor {

    private static final Logger log = LoggerFactory.getLogger(JwtChannelInterceptor.class);

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // Recuperar atributos definidos no handshake
            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

            if (sessionAttributes != null) {
                String username = (String) sessionAttributes.get("username");
                Long clienteId = (Long) sessionAttributes.get("clienteId");

                if (username != null) {
                    // Criar autenticação para o usuário
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_PREMIUM"))
                    );

                    accessor.setUser(auth);
                    log.debug("WebSocket STOMP: Usuário autenticado: {} (ID: {})", username, clienteId);
                }
            }
        }

        return message;
    }
}
