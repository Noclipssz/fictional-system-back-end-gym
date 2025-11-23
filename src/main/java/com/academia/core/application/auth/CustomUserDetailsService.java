package com.academia.core.application.auth;

import com.academia.core.domain.clientes.Cliente;
import com.academia.core.infrastructure.persistence.ClienteJpaRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final ClienteJpaRepository clienteRepository;

    public CustomUserDetailsService(ClienteJpaRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Cliente cliente = clienteRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Cliente não encontrado: " + username));

        return new org.springframework.security.core.userdetails.User(
                cliente.getUsername(),
                cliente.getSenha(),
                cliente.getActive(),
                true,
                true,
                true,
                getAuthorities()
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities() {
        // Todos os clientes têm ROLE_USER por padrão
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }
}
