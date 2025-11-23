package com.academia.core.application.auth;

import com.academia.core.domain.clientes.Cliente;
import com.academia.core.infrastructure.persistence.ClienteJpaRepository;
import com.academia.core.interfaces.auth.dto.AuthResponseDto;
import com.academia.core.interfaces.auth.dto.LoginRequestDto;
import com.academia.core.interfaces.auth.dto.RegisterRequestDto;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final ClienteJpaRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(
            ClienteJpaRepository clienteRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthResponseDto register(RegisterRequestDto request) {
        // Verificar se username já existe
        if (clienteRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username já está em uso");
        }

        // Verificar se email já existe
        if (clienteRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email já está em uso");
        }

        // Criar novo cliente (que também é o usuário)
        Cliente cliente = new Cliente();
        cliente.setUsername(request.getUsername());
        cliente.setEmail(request.getEmail());
        cliente.setSenha(passwordEncoder.encode(request.getPassword()));
        cliente.setNome(request.getNome());
        cliente.setActive(true);
        cliente.setPremium(false); // Cliente inicia como não premium

        Cliente savedCliente = clienteRepository.save(cliente);

        // Gerar token JWT
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(savedCliente.getUsername())
                .password(savedCliente.getSenha())
                .authorities("ROLE_USER")
                .build();

        String token = jwtService.generateToken(userDetails);

        return new AuthResponseDto(
                token,
                savedCliente.getId(),
                savedCliente.getUsername(),
                savedCliente.getEmail(),
                savedCliente.getNome()
        );
    }

    @Override
    public AuthResponseDto login(LoginRequestDto request) {
        // Autenticar
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Buscar cliente
        Cliente cliente = clienteRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        // Gerar token
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(cliente.getUsername())
                .password(cliente.getSenha())
                .authorities("ROLE_USER")
                .build();

        String token = jwtService.generateToken(userDetails);

        return new AuthResponseDto(
                token,
                cliente.getId(),
                cliente.getUsername(),
                cliente.getEmail(),
                cliente.getNome()
        );
    }

    @Override
    public Cliente getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return clienteRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
    }
}
