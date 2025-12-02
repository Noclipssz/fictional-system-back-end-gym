package com.academia.core.config.websocket;

import com.academia.core.application.auth.JwtService;
import com.academia.core.domain.clientes.Cliente;
import com.academia.core.infrastructure.persistence.ClienteJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * Interceptor para autenticação no handshake do WebSocket.
 * Valida o token JWT e verifica se o usuário é Premium.
 */
@Component
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private static final Logger log = LoggerFactory.getLogger(WebSocketAuthInterceptor.class);

    private final JwtService jwtService;
    private final ClienteJpaRepository clienteRepository;

    public WebSocketAuthInterceptor(JwtService jwtService, ClienteJpaRepository clienteRepository) {
        this.jwtService = jwtService;
        this.clienteRepository = clienteRepository;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {

        if (request instanceof ServletServerHttpRequest servletRequest) {
            String token = servletRequest.getServletRequest().getParameter("token");

            if (token == null || token.isEmpty()) {
                log.warn("WebSocket: Token não fornecido");
                return false;
            }

            try {
                // Extrair username do token
                String username = jwtService.extractUsername(token);

                if (username == null) {
                    log.warn("WebSocket: Username não encontrado no token");
                    return false;
                }

                // Buscar cliente no banco
                Cliente cliente = clienteRepository.findByUsername(username).orElse(null);

                if (cliente == null) {
                    log.warn("WebSocket: Cliente não encontrado: {}", username);
                    return false;
                }

                // Verificar se é Premium
                if (!Boolean.TRUE.equals(cliente.getPremium())) {
                    log.warn("WebSocket: Usuário não é Premium: {}", username);
                    return false;
                }

                // Armazenar informações do usuário nos atributos da sessão
                attributes.put("username", username);
                attributes.put("clienteId", cliente.getId());
                attributes.put("clienteNome", cliente.getNome());

                log.info("WebSocket: Conexão autorizada para usuário Premium: {}", username);
                return true;

            } catch (Exception e) {
                log.error("WebSocket: Erro ao validar token: {}", e.getMessage());
                return false;
            }
        }

        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // Nada a fazer após o handshake
    }
}
