package com.academia.core.application.treinos;

import com.academia.core.domain.treinos.Treino;

import java.util.List;
import java.util.Optional;

public interface TreinoService {

    Treino criarTreino(Treino treino);

    List<Treino> listarTreinos();

    Optional<Treino> buscarPorId(Long id);

    List<Treino> listarPorCliente(Long clienteId);
}
