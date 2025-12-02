package com.academia.core.application.chat;

import com.academia.core.domain.chat.Conversa;
import com.academia.core.domain.chat.Mensagem;
import com.academia.core.domain.chat.UsuarioOnline;
import com.academia.core.domain.clientes.Cliente;
import com.academia.core.infrastructure.chat.ConversaJpaRepository;
import com.academia.core.infrastructure.chat.MensagemJpaRepository;
import com.academia.core.infrastructure.chat.UsuarioOnlineJpaRepository;
import com.academia.core.infrastructure.persistence.ClienteJpaRepository;
import com.academia.core.interfaces.chat.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private final ConversaJpaRepository conversaRepository;
    private final MensagemJpaRepository mensagemRepository;
    private final UsuarioOnlineJpaRepository usuarioOnlineRepository;
    private final ClienteJpaRepository clienteRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatService(ConversaJpaRepository conversaRepository,
                       MensagemJpaRepository mensagemRepository,
                       UsuarioOnlineJpaRepository usuarioOnlineRepository,
                       ClienteJpaRepository clienteRepository,
                       SimpMessagingTemplate messagingTemplate) {
        this.conversaRepository = conversaRepository;
        this.mensagemRepository = mensagemRepository;
        this.usuarioOnlineRepository = usuarioOnlineRepository;
        this.clienteRepository = clienteRepository;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Envia uma mensagem privada para outro usuário
     */
    @Transactional
    public MensagemDto enviarMensagem(String remetenteUsername, Long destinatarioId, String conteudo) {
        // Buscar remetente
        Cliente remetente = clienteRepository.findByUsername(remetenteUsername)
                .orElseThrow(() -> new RuntimeException("Remetente não encontrado"));

        // Buscar destinatário
        Cliente destinatario = clienteRepository.findById(destinatarioId)
                .orElseThrow(() -> new RuntimeException("Destinatário não encontrado"));

        // Verificar se ambos são Premium
        if (!Boolean.TRUE.equals(remetente.getPremium()) || !Boolean.TRUE.equals(destinatario.getPremium())) {
            throw new RuntimeException("Ambos os usuários devem ser Premium para usar o chat");
        }

        // Buscar ou criar conversa
        Conversa conversa = getOrCreateConversa(remetente, destinatario);

        // Criar mensagem
        Mensagem mensagem = new Mensagem(conversa, remetente, conteudo);
        mensagem = mensagemRepository.save(mensagem);

        // Atualizar timestamp da última mensagem
        conversa.setUltimaMensagemAt(OffsetDateTime.now());
        conversaRepository.save(conversa);

        // Criar DTO
        MensagemDto mensagemDto = MensagemDto.fromEntity(mensagem);

        // Enviar para o destinatário via WebSocket
        messagingTemplate.convertAndSendToUser(
                destinatario.getUsername(),
                "/queue/mensagens",
                mensagemDto
        );

        log.info("Mensagem enviada de {} para {} (conversa ID: {})",
                remetenteUsername, destinatario.getUsername(), conversa.getId());

        return mensagemDto;
    }

    /**
     * Busca ou cria uma conversa entre dois usuários
     */
    @Transactional
    public Conversa getOrCreateConversa(Cliente usuario1, Cliente usuario2) {
        // Ordenar IDs para garantir consistência
        Long id1 = Math.min(usuario1.getId(), usuario2.getId());
        Long id2 = Math.max(usuario1.getId(), usuario2.getId());

        return conversaRepository.findByUsuarios(id1, id2)
                .orElseGet(() -> {
                    Cliente u1 = id1.equals(usuario1.getId()) ? usuario1 : usuario2;
                    Cliente u2 = id1.equals(usuario1.getId()) ? usuario2 : usuario1;
                    Conversa novaConversa = new Conversa(u1, u2);
                    return conversaRepository.save(novaConversa);
                });
    }

    /**
     * Lista todas as conversas de um usuário
     */
    @Transactional(readOnly = true)
    public List<ConversaDto> listarConversas(String username) {
        Cliente cliente = clienteRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<Conversa> conversas = conversaRepository.findByUsuarioId(cliente.getId());
        List<Long> idsOnline = usuarioOnlineRepository.findAllClienteIds();

        return conversas.stream().map(conversa -> {
            Long outroUsuarioId = conversa.getOutroUsuario(cliente.getId()).getId();
            boolean online = idsOnline.contains(outroUsuarioId);

            // Buscar última mensagem
            Mensagem ultimaMensagem = mensagemRepository.findTopByConversaIdOrderByCreatedAtDesc(conversa.getId());
            String textoUltimaMensagem = ultimaMensagem != null ? ultimaMensagem.getConteudo() : null;

            // Contar mensagens não lidas
            Long naoLidas = mensagemRepository.countNaoLidasPorConversa(conversa.getId(), cliente.getId());

            return ConversaDto.fromEntity(conversa, cliente.getId(), textoUltimaMensagem, naoLidas, online);
        }).collect(Collectors.toList());
    }

    /**
     * Busca mensagens de uma conversa com paginação
     */
    @Transactional(readOnly = true)
    public List<MensagemDto> buscarMensagens(Long conversaId, String username, int page, int size) {
        Cliente cliente = clienteRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Conversa conversa = conversaRepository.findById(conversaId)
                .orElseThrow(() -> new RuntimeException("Conversa não encontrada"));

        // Verificar se o usuário faz parte da conversa
        if (!conversa.contemUsuario(cliente.getId())) {
            throw new RuntimeException("Usuário não faz parte desta conversa");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Mensagem> mensagens = mensagemRepository.findByConversaId(conversaId, pageable);

        return mensagens.getContent().stream()
                .map(MensagemDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Marca mensagens de uma conversa como lidas
     */
    @Transactional
    public void marcarComoLidas(Long conversaId, String username) {
        Cliente cliente = clienteRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Conversa conversa = conversaRepository.findById(conversaId)
                .orElseThrow(() -> new RuntimeException("Conversa não encontrada"));

        if (!conversa.contemUsuario(cliente.getId())) {
            throw new RuntimeException("Usuário não faz parte desta conversa");
        }

        mensagemRepository.marcarComoLidas(conversaId, cliente.getId());
    }

    /**
     * Notifica que um usuário está digitando
     */
    @Transactional(readOnly = true)
    public void notificarDigitando(String username, Long conversaId, boolean digitando) {
        Cliente cliente = clienteRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Conversa conversa = conversaRepository.findById(conversaId)
                .orElseThrow(() -> new RuntimeException("Conversa não encontrada"));

        if (!conversa.contemUsuario(cliente.getId())) {
            return;
        }

        // Descobrir o outro usuário
        Cliente outroUsuario = conversa.getOutroUsuario(cliente.getId());

        // Enviar notificação para o outro usuário
        DigitandoDto dto = new DigitandoDto(conversaId, cliente.getId(), cliente.getNome(), digitando);
        messagingTemplate.convertAndSendToUser(
                outroUsuario.getUsername(),
                "/queue/digitando",
                dto
        );
    }

    /**
     * Lista todos os usuários Premium online
     */
    @Transactional(readOnly = true)
    public List<UsuarioOnlineDto> listarUsuariosOnline() {
        return usuarioOnlineRepository.findAllPremiumOnline().stream()
                .map(UsuarioOnlineDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Lista todos os usuários Premium (online e offline)
     */
    @Transactional(readOnly = true)
    public List<UsuarioOnlineDto> listarUsuariosPremium(String usernameExcluir) {
        List<Long> idsOnline = usuarioOnlineRepository.findAllClienteIds();

        // Buscar todos os clientes premium
        List<Cliente> premiumClientes = clienteRepository.findAll().stream()
                .filter(c -> Boolean.TRUE.equals(c.getPremium()))
                .filter(c -> !c.getUsername().equals(usernameExcluir))
                .collect(Collectors.toList());

        List<UsuarioOnlineDto> result = new ArrayList<>();
        for (Cliente cliente : premiumClientes) {
            boolean online = idsOnline.contains(cliente.getId());
            UsuarioOnlineDto dto = new UsuarioOnlineDto(
                    cliente.getId(),
                    cliente.getNome(),
                    cliente.getUsername(),
                    cliente.getAvatarDataUrl(),
                    online ? OffsetDateTime.now() : null
            );
            result.add(dto);
        }

        // Ordenar: online primeiro
        result.sort((a, b) -> {
            if (a.getConnectedAt() != null && b.getConnectedAt() == null) return -1;
            if (a.getConnectedAt() == null && b.getConnectedAt() != null) return 1;
            return a.getNome().compareToIgnoreCase(b.getNome());
        });

        return result;
    }

    /**
     * Verifica se um usuário está online
     */
    public boolean isUsuarioOnline(Long clienteId) {
        return usuarioOnlineRepository.existsByClienteId(clienteId);
    }

    /**
     * Inicia ou busca uma conversa com outro usuário
     */
    @Transactional
    public ConversaDto iniciarConversa(String username, Long outroUsuarioId) {
        Cliente cliente = clienteRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Cliente outroUsuario = clienteRepository.findById(outroUsuarioId)
                .orElseThrow(() -> new RuntimeException("Outro usuário não encontrado"));

        if (!Boolean.TRUE.equals(cliente.getPremium()) || !Boolean.TRUE.equals(outroUsuario.getPremium())) {
            throw new RuntimeException("Ambos os usuários devem ser Premium");
        }

        Conversa conversa = getOrCreateConversa(cliente, outroUsuario);
        boolean online = usuarioOnlineRepository.existsByClienteId(outroUsuarioId);

        return ConversaDto.fromEntity(conversa, cliente.getId(), null, 0L, online);
    }
}
