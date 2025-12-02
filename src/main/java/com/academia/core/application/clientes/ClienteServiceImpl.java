package com.academia.core.application.clientes;

import com.academia.core.common.CpfValidator;
import com.academia.core.domain.clientes.Cliente;
import com.academia.core.infrastructure.persistence.ClienteJpaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

        // Validar CPF (se fornecido)
        if (cliente.getCpf() != null && !cliente.getCpf().trim().isEmpty()) {
            String cpfSanitizado = CpfValidator.sanitize(cliente.getCpf());
            if (!CpfValidator.isValid(cpfSanitizado)) {
                throw new IllegalArgumentException("CPF inválido. Por favor, informe um CPF válido.");
            }
            cliente.setCpf(cpfSanitizado);

            // Validar CPF único
            Optional<Cliente> clienteComMesmoCpf = clienteRepository.findByCpf(cpfSanitizado);
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

                    // Validar e sanitizar CPF (se fornecido)
                    String cpfParaSalvar = null;
                    if (clienteAtualizado.getCpf() != null && !clienteAtualizado.getCpf().trim().isEmpty()) {
                        cpfParaSalvar = CpfValidator.sanitize(clienteAtualizado.getCpf());
                        if (!CpfValidator.isValid(cpfParaSalvar)) {
                            throw new IllegalArgumentException("CPF inválido. Por favor, informe um CPF válido.");
                        }

                        // Validar CPF único (se mudou) - sanitizar ambos para comparação correta
                        String cpfExistenteSanitizado = existente.getCpf() != null
                            ? CpfValidator.sanitize(existente.getCpf())
                            : null;
                        if (!cpfParaSalvar.equals(cpfExistenteSanitizado)) {
                            Optional<Cliente> clienteComMesmoCpf = clienteRepository.findByCpf(cpfParaSalvar);
                            if (clienteComMesmoCpf.isPresent() && !clienteComMesmoCpf.get().getId().equals(id)) {
                                throw new IllegalArgumentException("CPF já está em uso por outro cliente");
                            }
                        }
                    }

                    // Atualiza apenas os campos que foram fornecidos (não nulos)
                    if (clienteAtualizado.getNome() != null) {
                        existente.setNome(clienteAtualizado.getNome());
                    }
                    if (clienteAtualizado.getUsername() != null) {
                        existente.setUsername(clienteAtualizado.getUsername());
                    }
                    if (clienteAtualizado.getEmail() != null) {
                        existente.setEmail(clienteAtualizado.getEmail());
                    }
                    if (clienteAtualizado.getTelefone() != null) {
                        existente.setTelefone(clienteAtualizado.getTelefone());
                    }
                    if (cpfParaSalvar != null) {
                        existente.setCpf(cpfParaSalvar);
                    }
                    if (clienteAtualizado.getEndereco() != null) {
                        existente.setEndereco(clienteAtualizado.getEndereco());
                    }
                    if (clienteAtualizado.getDataNascimento() != null) {
                        existente.setDataNascimento(clienteAtualizado.getDataNascimento());
                    }
                    if (clienteAtualizado.getAvatarDataUrl() != null) {
                        existente.setAvatarDataUrl(clienteAtualizado.getAvatarDataUrl());
                    }
                    // Criptografar senha se foi fornecida
                    if (clienteAtualizado.getSenha() != null && !clienteAtualizado.getSenha().trim().isEmpty()) {
                        existente.setSenha(passwordEncoder.encode(clienteAtualizado.getSenha()));
                    }
                    // Campos que usuário NÃO pode atualizar via perfil (mantém os existentes):
                    // - premium (somente admin/sistema pode alterar)
                    // - premiumAte (somente admin/sistema pode alterar)
                    // - active (somente admin/sistema pode alterar)

                    // updatedAt é @UpdateTimestamp, Hibernate cuida
                    return clienteRepository.save(existente);
                })
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado para atualizar"));
    }

    @Override
    public Cliente atualizarStatusPremium(Long id, boolean premium, LocalDate premiumAte) {
        return clienteRepository.findById(id)
                .map(cliente -> {
                    cliente.setPremium(premium);
                    cliente.setPremiumAte(premium ? premiumAte : null);
                    return clienteRepository.save(cliente);
                })
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado para atualizar premium"));
    }

    @Override
    public void atualizarSenha(Long id, String novaSenhaEncoded) {
        clienteRepository.findById(id)
                .map(cliente -> {
                    cliente.setSenha(novaSenhaEncoded);
                    return clienteRepository.save(cliente);
                })
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado para atualizar senha"));
    }

    @Override
    public void excluirCliente(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new IllegalArgumentException("Cliente não encontrado para exclusão");
        }
        clienteRepository.deleteById(id);
    }
}
