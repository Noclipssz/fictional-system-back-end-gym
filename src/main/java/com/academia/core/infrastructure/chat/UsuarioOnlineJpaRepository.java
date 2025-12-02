package com.academia.core.infrastructure.chat;

import com.academia.core.domain.chat.UsuarioOnline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioOnlineJpaRepository extends JpaRepository<UsuarioOnline, Long> {

    /**
     * Busca usuário online por session ID
     */
    Optional<UsuarioOnline> findBySessionId(String sessionId);

    /**
     * Verifica se um usuário está online
     */
    boolean existsByClienteId(Long clienteId);

    /**
     * Busca todos os usuários online que são Premium
     */
    @Query("SELECT u FROM UsuarioOnline u WHERE u.cliente.premium = true")
    List<UsuarioOnline> findAllPremiumOnline();

    /**
     * Busca IDs de todos os usuários online
     */
    @Query("SELECT u.clienteId FROM UsuarioOnline u")
    List<Long> findAllClienteIds();

    /**
     * Remove usuário online por session ID
     */
    void deleteBySessionId(String sessionId);

    /**
     * Verifica se usuários específicos estão online
     */
    @Query("SELECT u.clienteId FROM UsuarioOnline u WHERE u.clienteId IN :ids")
    List<Long> findOnlineByIds(@Param("ids") List<Long> ids);
}
