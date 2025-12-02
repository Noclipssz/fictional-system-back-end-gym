package com.academia.core.interfaces.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para eventos de presença (online/offline)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PresencaEventDto {
    private Long clienteId;
    private String username;
    private String nome;
    private boolean online;
}
