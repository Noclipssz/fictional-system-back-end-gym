package com.academia.core.application.auth;

import com.academia.core.domain.clientes.Cliente;
import com.academia.core.interfaces.auth.dto.AuthResponseDto;
import com.academia.core.interfaces.auth.dto.LoginRequestDto;
import com.academia.core.interfaces.auth.dto.RegisterRequestDto;

public interface AuthService {
    AuthResponseDto register(RegisterRequestDto request);
    AuthResponseDto login(LoginRequestDto request);
    Cliente getCurrentUser();
}
