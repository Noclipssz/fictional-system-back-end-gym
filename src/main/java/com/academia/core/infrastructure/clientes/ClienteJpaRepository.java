package com.academia.core.infrastructure.clientes;

import com.academia.core.domain.clientes.Cliente;
import com.academia.core.domain.clientes.ClienteRepositoryPort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteJpaRepository extends JpaRepository<Cliente, Long>, ClienteRepositoryPort {

    @Override
    Cliente save(Cliente c);

    @Override
    List<Cliente> findAll();

    @Override
    Optional<Cliente> findById(Long id);
}
