package com.academia.core.interfaces.clientes;

import com.academia.core.domain.clientes.Cliente;
import com.academia.core.interfaces.clientes.dto.ClienteResponseDto;

public class ClienteMapper {

    public static ClienteResponseDto toResponseDto(Cliente c) {
        if (c == null) {
            return null;
        }

        return new ClienteResponseDto(
                c.getId(),
                c.getNome(),
                c.getEmail(),
                c.getTelefone(),
                c.getCpf(),
                c.getEndereco(),
                c.getDataNascimento() != null ? c.getDataNascimento().toString() : null,
                c.getPremium(),
                c.getPremiumAte() != null ? c.getPremiumAte().toString() : null,
                c.getAvatarDataUrl()
        );
    }
}
