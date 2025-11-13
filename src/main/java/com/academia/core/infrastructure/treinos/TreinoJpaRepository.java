package com.academia.core.infrastructure.treinos;

import com.academia.core.domain.treinos.Treino;
import com.academia.core.domain.treinos.TreinoRepositoryPort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TreinoJpaRepository extends JpaRepository<Treino, Long>, TreinoRepositoryPort {

    @Override
    Treino save(Treino t);

    @Override
    List<Treino> findAll();

    List<Treino> findByClienteId(Long clienteId);
}
