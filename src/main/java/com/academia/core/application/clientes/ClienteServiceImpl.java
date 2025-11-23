package com.academia.core.application.clientes;

import com.academia.core.domain.clientes.Cliente;
import com.academia.core.infrastructure.persistence.ClienteJpaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteJpaRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    public ClienteServiceImpl(ClienteJpaRepository clienteRepository, PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
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
        // Validações de negócio
        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do cliente é obrigatório");
        }

        if (cliente.getUsername() == null || cliente.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username do cliente é obrigatório");
        }

        if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email do cliente é obrigatório");
        }

        if (cliente.getSenha() == null || cliente.getSenha().trim().isEmpty()) {
            throw new IllegalArgumentException("Senha do cliente é obrigatória");
        }

        // Validar username único
        Optional<Cliente> clienteComMesmoUsername = clienteRepository.findByUsername(cliente.getUsername());
        if (clienteComMesmoUsername.isPresent()) {
            throw new IllegalArgumentException("Username já está em uso por outro cliente");
        }

        // Validar email único
        Optional<Cliente> clienteExistente = clienteRepository.findByEmail(cliente.getEmail());
        if (clienteExistente.isPresent()) {
            throw new IllegalArgumentException("Email já está em uso por outro cliente");
        }

        // Validar CPF único (se fornecido)
        if (cliente.getCpf() != null && !cliente.getCpf().trim().isEmpty()) {
            Optional<Cliente> clienteComMesmoCpf = clienteRepository.findByCpf(cliente.getCpf());
            if (clienteComMesmoCpf.isPresent()) {
                throw new IllegalArgumentException("CPF já está em uso por outro cliente");
            }
        }

        // Criptografar a senha antes de salvar
        cliente.setSenha(passwordEncoder.encode(cliente.getSenha()));

        // Garantir que o cliente inicia como ativo
        if (cliente.getActive() == null) {
            cliente.setActive(true);
        }

        try {
            return clienteRepository.save(cliente);
        } catch (DataIntegrityViolationException e) {
            // Capturar erros de constraint do banco
            if (e.getMessage().contains("username")) {
                throw new IllegalArgumentException("Username já existe no sistema", e);
            } else if (e.getMessage().contains("email")) {
                throw new IllegalArgumentException("Email já existe no sistema", e);
            } else if (e.getMessage().contains("cpf")) {
                throw new IllegalArgumentException("CPF já existe no sistema", e);
            }
            throw new IllegalArgumentException("Erro ao salvar cliente: violação de integridade de dados", e);
        }
    }

    @Override
    public Cliente atualizarCliente(Long id, Cliente clienteAtualizado) {
        return clienteRepository.findById(id)
                .map(existente -> {
                    // Validar username único (se mudou)
                    if (clienteAtualizado.getUsername() != null && !existente.getUsername().equals(clienteAtualizado.getUsername())) {
                        Optional<Cliente> clienteComMesmoUsername = clienteRepository.findByUsername(clienteAtualizado.getUsername());
                        if (clienteComMesmoUsername.isPresent() && !clienteComMesmoUsername.get().getId().equals(id)) {
                            throw new IllegalArgumentException("Username já está em uso por outro cliente");
                        }
                    }

                    // Validar email único (se mudou)
                    if (!existente.getEmail().equals(clienteAtualizado.getEmail())) {
                        Optional<Cliente> clienteComMesmoEmail = clienteRepository.findByEmail(clienteAtualizado.getEmail());
                        if (clienteComMesmoEmail.isPresent() && !clienteComMesmoEmail.get().getId().equals(id)) {
                            throw new IllegalArgumentException("Email já está em uso por outro cliente");
                        }
                    }

                    // Validar CPF único (se mudou e foi fornecido)
                    if (clienteAtualizado.getCpf() != null && !clienteAtualizado.getCpf().equals(existente.getCpf())) {
                        Optional<Cliente> clienteComMesmoCpf = clienteRepository.findByCpf(clienteAtualizado.getCpf());
                        if (clienteComMesmoCpf.isPresent() && !clienteComMesmoCpf.get().getId().equals(id)) {
                            throw new IllegalArgumentException("CPF já está em uso por outro cliente");
                        }
                    }

                    // Atualiza todos os campos editáveis do perfil:
                    existente.setNome(clienteAtualizado.getNome());
                    if (clienteAtualizado.getUsername() != null) {
                        existente.setUsername(clienteAtualizado.getUsername());
                    }
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
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado para atualizar"));
    }
}
