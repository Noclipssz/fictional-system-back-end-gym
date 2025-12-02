package com.academia.core.infrastructure.chat;

import com.academia.core.domain.chat.Conversa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversaJpaRepository extends JpaRepository<Conversa, Long> {

    /**
     * Busca uma conversa entre dois usuários (independente da ordem)
     */
    @Query("SELECT c FROM Conversa c WHERE " +
           "(c.usuario1.id = :usuario1Id AND c.usuario2.id = :usuario2Id) OR " +
           "(c.usuario1.id = :usuario2Id AND c.usuario2.id = :usuario1Id)")
    Optional<Conversa> findByUsuarios(@Param("usuario1Id") Long usuario1Id,
                                       @Param("usuario2Id") Long usuario2Id);

    /**
     * Busca todas as conversas de um usuário, ordenadas pela última mensagem
     */
    @Query("SELECT c FROM Conversa c WHERE c.usuario1.id = :usuarioId OR c.usuario2.id = :usuarioId " +
           "ORDER BY c.ultimaMensagemAt DESC")
    List<Conversa> findByUsuarioId(@Param("usuarioId") Long usuarioId);

    /**
     * Conta conversas com mensagens não lidas para um usuário
     */
    @Query("SELECT COUNT(DISTINCT m.conversa.id) FROM Mensagem m " +
           "WHERE m.lida = false AND m.remetente.id != :usuarioId " +
           "AND (m.conversa.usuario1.id = :usuarioId OR m.conversa.usuario2.id = :usuarioId)")
    Long countConversasComMensagensNaoLidas(@Param("usuarioId") Long usuarioId);
}
