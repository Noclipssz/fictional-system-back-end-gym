package com.academia.core.interfaces.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para indicador de "digitando..."
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DigitandoDto {
    private Long conversaId;
    private Long usuarioId;
    private String usuarioNome;
    private boolean digitando;
}
