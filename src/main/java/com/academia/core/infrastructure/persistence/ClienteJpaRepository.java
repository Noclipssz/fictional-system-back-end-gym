package com.academia.core.infrastructure.persistence;

import com.academia.core.domain.clientes.Cliente;
import com.academia.core.domain.clientes.ClienteRepositoryPort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteJpaRepository extends JpaRepository<Cliente, Long>, ClienteRepositoryPort {

    // Métodos para autenticação
    Optional<Cliente> findByEmail(String email);
    Optional<Cliente> findByUsername(String username);

    // Método para validar CPF único
    Optional<Cliente> findByCpf(String cpf);

    // Métodos herdados do ClienteRepositoryPort (já implementados pelo JpaRepository)
    @Override
    Cliente save(Cliente c);

    @Override
    List<Cliente> findAll();

    @Override
    Optional<Cliente> findById(Long id);
}
