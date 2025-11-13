package com.academia.core.application.treinos;

import com.academia.core.domain.treinos.Treino;
import com.academia.core.domain.treinos.TreinoRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class TreinoServiceImpl implements TreinoService {

    private final TreinoRepositoryPort treinoRepository;

    public TreinoServiceImpl(TreinoRepositoryPort treinoRepository) {
        this.treinoRepository = treinoRepository;
    }

    @Override
    public Treino criarTreino(Treino treino) {
        treino.setCreatedAt(Instant.now());
        return treinoRepository.save(treino);
    }

    @Override
    public List<Treino> listarTreinos() {
        return treinoRepository.findAll();
    }

    @Override
    public Optional<Treino> buscarPorId(Long id) {
        return treinoRepository.findById(id);
    }

    @Override
    public List<Treino> listarPorCliente(Long clienteId) {
        return treinoRepository.findByClienteId(clienteId);
    }
}
