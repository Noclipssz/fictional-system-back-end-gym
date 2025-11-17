package com.academia.core.interfaces.bff;

import com.academia.core.application.auth.ClienteAuthService;
import com.academia.core.common.ApiResponse;
import com.academia.core.interfaces.auth.dto.AuthResponseDto;
import com.academia.core.interfaces.auth.dto.LoginRequestDto;
import com.academia.core.interfaces.auth.dto.RegisterRequestDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bff/auth")
@CrossOrigin(origins = "*")
public class BffAuthController {

    private final ClienteAuthService authService;

    public BffAuthController(ClienteAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<ApiResponse<AuthResponseDto>> cadastro(@Valid @RequestBody RegisterRequestDto request) {
        try {
            AuthResponseDto response = authService.register(request);
            return ResponseEntity
                    .status(201)
                    .body(ApiResponse.ok(response, "Cadastro realizado com sucesso"));
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
                    .body(ApiResponse.fail("Email ou senha inválidos"));
        }
    }
}
