package com.academia.core.application.clientes;

import com.academia.core.domain.clientes.Cliente;
import com.academia.core.infrastructure.persistence.ClienteJpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteJpaRepository clienteRepository;

    public ClienteServiceImpl(ClienteJpaRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    @Override
    public Cliente criarCliente(Cliente cliente) {
        // poderia validar campos obrigatórios etc.
        return clienteRepository.save(cliente);
    }

    @Override
    public Cliente atualizarCliente(Long id, Cliente clienteAtualizado) {
        return clienteRepository.findById(id)
                .map(existente -> {
                    // Atualiza todos os campos editáveis do perfil:
                    existente.setNome(clienteAtualizado.getNome());
                    existente.setEmail(clienteAtualizado.getEmail());
                    existente.setTelefone(clienteAtualizado.getTelefone());
                    existente.setCpf(clienteAtualizado.getCpf());
                    existente.setEndereco(clienteAtualizado.getEndereco());
                    existente.setDataNascimento(clienteAtualizado.getDataNascimento());
                    existente.setPremium(clienteAtualizado.getPremium());
                    existente.setPremiumAte(clienteAtualizado.getPremiumAte());
                    existente.setAvatarDataUrl(clienteAtualizado.getAvatarDataUrl());
                    // updatedAt é @UpdateTimestamp, Hibernate cuida
                    return clienteRepository.save(existente);
                })
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado para atualizar"));
    }
}
