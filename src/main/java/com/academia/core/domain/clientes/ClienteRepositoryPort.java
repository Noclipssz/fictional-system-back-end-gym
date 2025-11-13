package com.academia.core.domain.clientes;

import java.util.List;
import java.util.Optional;

public interface ClienteRepositoryPort {
    Cliente save(Cliente c);
    List<Cliente> findAll();
    Optional<Cliente> findById(Long id);
}
