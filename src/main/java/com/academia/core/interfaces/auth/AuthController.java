package com.academia.core.interfaces.auth;

import com.academia.core.application.auth.AuthService;
import com.academia.core.common.ApiResponse;
import com.academia.core.domain.clientes.Cliente;
import com.academia.core.interfaces.auth.dto.AuthResponseDto;
import com.academia.core.interfaces.auth.dto.LoginRequestDto;
import com.academia.core.interfaces.auth.dto.RegisterRequestDto;
import com.academia.core.interfaces.clientes.ClienteMapper;
import com.academia.core.interfaces.clientes.dto.ClienteResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponseDto>> register(@Valid @RequestBody RegisterRequestDto request) {
        try {
            AuthResponseDto response = authService.register(request);
            return ResponseEntity
                    .status(201)
                    .body(ApiResponse.ok(response, "Usuário registrado com sucesso"));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.fail(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDto>> login(@Valid @RequestBody LoginRequestDto request) {
        try {
            AuthResponseDto response = authService.login(request);
            return ResponseEntity.ok(
                    ApiResponse.ok(response, "Login realizado com sucesso")
            );
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(401)
                    .body(ApiResponse.fail("Credenciais inválidas"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ClienteResponseDto>> getCurrentUser() {
        try {
            Cliente cliente = authService.getCurrentUser();
            ClienteResponseDto dto = ClienteMapper.toResponseDto(cliente);
            return ResponseEntity.ok(
                    ApiResponse.ok(dto, "Usuário autenticado")
            );
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(401)
                    .body(ApiResponse.fail("Não autenticado"));
        }
    }
}
