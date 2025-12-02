package com.academia.core.config.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthInterceptor webSocketAuthInterceptor;
    private final JwtChannelInterceptor jwtChannelInterceptor;

    public WebSocketConfig(WebSocketAuthInterceptor webSocketAuthInterceptor,
                           JwtChannelInterceptor jwtChannelInterceptor) {
        this.webSocketAuthInterceptor = webSocketAuthInterceptor;
        this.jwtChannelInterceptor = jwtChannelInterceptor;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Prefixo para tópicos de broadcast (servidor -> clientes)
        // /topic = broadcast para todos
        // /queue = mensagens privadas
        registry.enableSimpleBroker("/topic", "/queue");

        // Prefixo para mensagens enviadas do cliente para o servidor
        registry.setApplicationDestinationPrefixes("/app");

        // Prefixo para mensagens direcionadas a usuários específicos
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint WebSocket com SockJS fallback
        registry.addEndpoint("/ws")
                .addInterceptors(webSocketAuthInterceptor)
                .setAllowedOriginPatterns("*")
                .withSockJS();

        // Endpoint WebSocket puro (sem SockJS)
        registry.addEndpoint("/ws")
                .addInterceptors(webSocketAuthInterceptor)
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // Interceptor para validar mensagens STOMP
        registration.interceptors(jwtChannelInterceptor);
    }
}
