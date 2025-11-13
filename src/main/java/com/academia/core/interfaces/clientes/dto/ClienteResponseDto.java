package com.academia.core.interfaces.clientes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponseDto {

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String cpf;
    private String endereco;
    private String dataNascimento; // yyyy-MM-dd
    private Boolean premium;
    private String premiumAte; // yyyy-MM-dd
    private String avatarDataUrl;

    // você pode adicionar createdAt/updatedAt se quiser exibir depois
}
