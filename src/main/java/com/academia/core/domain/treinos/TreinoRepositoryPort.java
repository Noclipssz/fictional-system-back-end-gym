package com.academia.core.domain.treinos;

import java.util.List;
import java.util.Optional;

public interface TreinoRepositoryPort {
    Treino save(Treino t);
    List<Treino> findAll();
    Optional<Treino> findById(Long id);
    List<Treino> findByClienteId(Long clienteId);
}
