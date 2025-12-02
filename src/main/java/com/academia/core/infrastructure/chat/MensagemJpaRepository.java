package com.academia.core.infrastructure.chat;

import com.academia.core.domain.chat.Mensagem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensagemJpaRepository extends JpaRepository<Mensagem, Long> {

    /**
     * Busca mensagens de uma conversa, ordenadas por data (mais recentes primeiro)
     */
    @Query("SELECT m FROM Mensagem m WHERE m.conversa.id = :conversaId ORDER BY m.createdAt DESC")
    Page<Mensagem> findByConversaId(@Param("conversaId") Long conversaId, Pageable pageable);

    /**
     * Busca as últimas N mensagens de uma conversa (ordem cronológica)
     */
    @Query("SELECT m FROM Mensagem m WHERE m.conversa.id = :conversaId ORDER BY m.createdAt ASC")
    List<Mensagem> findUltimasMensagens(@Param("conversaId") Long conversaId, Pageable pageable);

    /**
     * Conta mensagens não lidas em uma conversa para um usuário
     */
    @Query("SELECT COUNT(m) FROM Mensagem m WHERE m.conversa.id = :conversaId " +
           "AND m.lida = false AND m.remetente.id != :usuarioId")
    Long countNaoLidasPorConversa(@Param("conversaId") Long conversaId,
                                   @Param("usuarioId") Long usuarioId);

    /**
     * Marca todas as mensagens de uma conversa como lidas (exceto as enviadas pelo próprio usuário)
     */
    @Modifying
    @Query("UPDATE Mensagem m SET m.lida = true WHERE m.conversa.id = :conversaId " +
           "AND m.remetente.id != :usuarioId AND m.lida = false")
    int marcarComoLidas(@Param("conversaId") Long conversaId, @Param("usuarioId") Long usuarioId);

    /**
     * Busca a última mensagem de uma conversa
     */
    Mensagem findTopByConversaIdOrderByCreatedAtDesc(Long conversaId);
}
