package com.academia.core.config.websocket;

import com.academia.core.domain.chat.UsuarioOnline;
import com.academia.core.domain.clientes.Cliente;
import com.academia.core.infrastructure.chat.UsuarioOnlineJpaRepository;
import com.academia.core.infrastructure.persistence.ClienteJpaRepository;
import com.academia.core.interfaces.chat.dto.PresencaEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Map;

/**
 * Listener para eventos de conexão/desconexão WebSocket.
 * Gerencia a tabela de usuários online e notifica sobre presença.
 */
@Component
public class WebSocketEventListener {

    private static final Logger log = LoggerFactory.getLogger(WebSocketEventListener.class);

    private final UsuarioOnlineJpaRepository usuarioOnlineRepository;
    private final ClienteJpaRepository clienteRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketEventListener(UsuarioOnlineJpaRepository usuarioOnlineRepository,
                                   ClienteJpaRepository clienteRepository,
                                   SimpMessagingTemplate messagingTemplate) {
        this.usuarioOnlineRepository = usuarioOnlineRepository;
        this.clienteRepository = clienteRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    @Transactional
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        // Tentar obter dados de sessionAttributes primeiro
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        Long clienteId = null;
        String username = null;
        String nome = null;

        if (sessionAttributes != null) {
            clienteId = (Long) sessionAttributes.get("clienteId");
            username = (String) sessionAttributes.get("username");
            nome = (String) sessionAttributes.get("clienteNome");
        }

        // Se não encontrou nos sessionAttributes, tentar via simpConnectMessage
        if (username == null) {
            Message<?> connectMessage = (Message<?>) headerAccessor.getHeader("simpConnectMessage");
            if (connectMessage != null) {
                StompHeaderAccessor connectAccessor = StompHeaderAccessor.wrap(connectMessage);
                Map<String, Object> connectSessionAttrs = connectAccessor.getSessionAttributes();
                if (connectSessionAttrs != null) {
                    clienteId = (Long) connectSessionAttrs.get("clienteId");
                    username = (String) connectSessionAttrs.get("username");
                    nome = (String) connectSessionAttrs.get("clienteNome");
                }
                // Também tentar o Principal do connectMessage
                if (username == null) {
                    Principal user = connectAccessor.getUser();
                    if (user != null) {
                        username = user.getName();
                    }
                }
            }
        }

        // Ainda sem username? Tentar o user do headerAccessor
        if (username == null) {
            Principal user = headerAccessor.getUser();
            if (user != null) {
                username = user.getName();
            }
        }

        log.info("SessionConnected - sessionId: {}, username: {}, clienteId: {}", sessionId, username, clienteId);

        if (username != null) {
            // Buscar cliente pelo username se não temos o ID
            Cliente cliente = null;
            if (clienteId != null) {
                cliente = clienteRepository.findById(clienteId).orElse(null);
            }
            if (cliente == null) {
                cliente = clienteRepository.findByUsername(username).orElse(null);
            }

            if (cliente != null) {
                final String finalUsername = username;
                // Verificar se já existe uma sessão para este cliente (reconexão)
                usuarioOnlineRepository.findById(cliente.getId()).ifPresent(existing -> {
                    log.info("Removendo sessão antiga para usuário: {}", finalUsername);
                    usuarioOnlineRepository.delete(existing);
                });

                UsuarioOnline usuarioOnline = new UsuarioOnline(cliente, sessionId);
                usuarioOnlineRepository.save(usuarioOnline);

                log.info("Usuário registrado como online: {} (ID: {}, Session: {})", finalUsername, cliente.getId(), sessionId);

                // Notificar todos sobre o novo usuário online
                PresencaEventDto presencaEvent = new PresencaEventDto(
                        cliente.getId(),
                        finalUsername,
                        cliente.getNome(),
                        true
                );
                messagingTemplate.convertAndSend("/topic/presenca", presencaEvent);
            } else {
                log.warn("Cliente não encontrado para username: {}", username);
            }
        } else {
            log.warn("SessionConnected sem username - sessionId: {}", sessionId);
        }
    }

    @EventListener
    @Transactional
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        log.info("SessionDisconnect - sessionId: {}", sessionId);

        // Buscar usuário pela sessão
        usuarioOnlineRepository.findBySessionId(sessionId).ifPresent(usuarioOnline -> {
            Cliente cliente = usuarioOnline.getCliente();
            Long clienteId = cliente.getId();
            String username = cliente.getUsername();
            String nome = cliente.getNome();

            // Remover da tabela de online
            usuarioOnlineRepository.delete(usuarioOnline);

            log.info("Usuário desconectado: {} (ID: {}, Session: {})", username, clienteId, sessionId);

            // Notificar todos sobre o usuário que saiu
            PresencaEventDto presencaEvent = new PresencaEventDto(
                    clienteId,
                    username,
                    nome,
                    false
            );
            messagingTemplate.convertAndSend("/topic/presenca", presencaEvent);
        });
    }
}
