package com.academia.core.application.auth;

import com.academia.core.domain.clientes.Cliente;
import com.academia.core.infrastructure.persistence.ClienteJpaRepository;
import com.academia.core.interfaces.auth.dto.AuthResponseDto;
import com.academia.core.interfaces.auth.dto.LoginRequestDto;
import com.academia.core.interfaces.auth.dto.RegisterRequestDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClienteAuthService {

    private final ClienteJpaRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public ClienteAuthService(ClienteJpaRepository clienteRepository,
                              PasswordEncoder passwordEncoder,
                              JwtService jwtService) {
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponseDto register(RegisterRequestDto request) {
        // Verificar se email já existe
        if (clienteRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }

        // Criar novo cliente
        Cliente cliente = new Cliente();
        cliente.setNome(request.getNome());
        cliente.setEmail(request.getEmail());
        cliente.setSenha(passwordEncoder.encode(request.getPassword()));
        cliente.setPremium(false);

        // Campos opcionais do cadastro
        if (request.getTelefone() != null) {
            cliente.setTelefone(request.getTelefone());
        }
        if (request.getCpf() != null) {
            cliente.setCpf(request.getCpf());
        }
        if (request.getEndereco() != null) {
            cliente.setEndereco(request.getEndereco());
        }
        if (request.getDataNascimento() != null) {
            cliente.setDataNascimento(request.getDataNascimento());
        }

        // Salvar cliente
        Cliente saved = clienteRepository.save(cliente);

        // Gerar token JWT
        String token = jwtService.generateToken(saved.getEmail());

        return new AuthResponseDto(
                token,
                saved.getId(),
                saved.getEmail(),
                saved.getEmail(),
                saved.getNome()
        );
    }

    public AuthResponseDto login(LoginRequestDto request) {
        // Buscar cliente por email (username é email)
        Optional<Cliente> clienteOpt = clienteRepository.findByEmail(request.getUsername());

        if (!clienteOpt.isPresent()) {
            throw new RuntimeException("Email ou senha inválidos");
        }

        Cliente cliente = clienteOpt.get();

        // Verificar senha
        if (!passwordEncoder.matches(request.getPassword(), cliente.getSenha())) {
            throw new RuntimeException("Email ou senha inválidos");
        }

        // Gerar token JWT
        String token = jwtService.generateToken(cliente.getEmail());

        return new AuthResponseDto(
                token,
                cliente.getId(),
                cliente.getEmail(),
                cliente.getEmail(),
                cliente.getNome()
        );
    }
}
