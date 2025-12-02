package com.academia.core.interfaces.chat;

import com.academia.core.application.chat.ChatService;
import com.academia.core.common.ApiResponse;
import com.academia.core.interfaces.chat.dto.ConversaDto;
import com.academia.core.interfaces.chat.dto.MensagemDto;
import com.academia.core.interfaces.chat.dto.UsuarioOnlineDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para operações de chat (histórico, conversas, etc.)
 * Estes endpoints passarão pelo BFF Laravel
 */
@RestController
@RequestMapping("/api/chat")
public class ChatRestController {

    private static final Logger log = LoggerFactory.getLogger(ChatRestController.class);

    private final ChatService chatService;

    public ChatRestController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Lista todas as conversas do usuário
     */
    @GetMapping("/conversas")
    public ResponseEntity<ApiResponse<List<ConversaDto>>> listarConversas(
            @AuthenticationPrincipal UserDetails userDetails) {

        log.debug("Listando conversas para usuário: {}", userDetails.getUsername());

        List<ConversaDto> conversas = chatService.listarConversas(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(conversas));
    }

    /**
     * Busca mensagens de uma conversa com paginação
     */
    @GetMapping("/conversas/{conversaId}/mensagens")
    public ResponseEntity<ApiResponse<List<MensagemDto>>> buscarMensagens(
            @PathVariable Long conversaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @AuthenticationPrincipal UserDetails userDetails) {

        log.debug("Buscando mensagens da conversa {} para usuário: {}", conversaId, userDetails.getUsername());

        List<MensagemDto> mensagens = chatService.buscarMensagens(
                conversaId,
                userDetails.getUsername(),
                page,
                size
        );
        return ResponseEntity.ok(ApiResponse.ok(mensagens));
    }

    /**
     * Inicia ou busca uma conversa com outro usuário
     */
    @PostMapping("/conversas/iniciar/{outroUsuarioId}")
    public ResponseEntity<ApiResponse<ConversaDto>> iniciarConversa(
            @PathVariable Long outroUsuarioId,
            @AuthenticationPrincipal UserDetails userDetails) {

        log.debug("Iniciando conversa entre {} e usuário {}", userDetails.getUsername(), outroUsuarioId);

        ConversaDto conversa = chatService.iniciarConversa(userDetails.getUsername(), outroUsuarioId);
        return ResponseEntity.ok(ApiResponse.ok(conversa));
    }

    /**
     * Marca mensagens de uma conversa como lidas
     */
    @PostMapping("/conversas/{conversaId}/lidas")
    public ResponseEntity<ApiResponse<Void>> marcarComoLidas(
            @PathVariable Long conversaId,
            @AuthenticationPrincipal UserDetails userDetails) {

        log.debug("Marcando mensagens como lidas: conversa {}, usuário {}", conversaId, userDetails.getUsername());

        chatService.marcarComoLidas(conversaId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(null, "Mensagens marcadas como lidas"));
    }

    /**
     * Lista usuários Premium (para iniciar conversas)
     */
    @GetMapping("/usuarios")
    public ResponseEntity<ApiResponse<List<UsuarioOnlineDto>>> listarUsuariosPremium(
            @AuthenticationPrincipal UserDetails userDetails) {

        log.debug("Listando usuários Premium para: {}", userDetails.getUsername());

        List<UsuarioOnlineDto> usuarios = chatService.listarUsuariosPremium(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(usuarios));
    }

    /**
     * Lista apenas usuários online
     */
    @GetMapping("/usuarios/online")
    public ResponseEntity<ApiResponse<List<UsuarioOnlineDto>>> listarUsuariosOnline() {
        List<UsuarioOnlineDto> usuarios = chatService.listarUsuariosOnline();
        return ResponseEntity.ok(ApiResponse.ok(usuarios));
    }
}
