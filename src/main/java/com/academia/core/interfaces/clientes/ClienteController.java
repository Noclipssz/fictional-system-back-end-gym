package com.academia.core.interfaces.clientes;

import com.academia.core.application.auth.AuthService;
import com.academia.core.application.clientes.ClienteService;
import com.academia.core.domain.clientes.Cliente;
import com.academia.core.interfaces.clientes.dto.ClienteResponseDto;
import com.academia.core.common.ApiResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private static final Logger log = LoggerFactory.getLogger(ClienteController.class);

    private final ClienteService clienteService;
    private final AuthService authService;

    public ClienteController(ClienteService clienteService, AuthService authService) {
        this.clienteService = clienteService;
        this.authService = authService;
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> atualizar(
            @PathVariable("id") Long id,
            @RequestBody Cliente body
    ) {
        try {
            log.info("Tentativa de atualizar perfil: id={}", id);

            // Validar que o usuário está atualizando apenas o próprio perfil
            Cliente currentUser = authService.getCurrentUser();
            log.info("Usuário autenticado: id={}, username={}",
                    currentUser.getId(), currentUser.getUsername());

            if (!currentUser.getId().equals(id)) {
                log.warn("Tentativa de atualizar perfil de outro usuário. Current={}, Target={}",
                        currentUser.getId(), id);
                return ResponseEntity
                        .status(403)
                        .body(ApiResponse.fail("Você só pode atualizar seu próprio perfil"));
            }

            log.info("Autorização OK. Atualizando perfil id={}", id);

            Cliente atualizado = clienteService.atualizarCliente(id, body);
            ClienteResponseDto dto = ClienteMapper.toResponseDto(atualizado);

            return ResponseEntity.ok(
                    ApiResponse.ok(dto, "Perfil atualizado com sucesso")
            );
        } catch (IllegalArgumentException e) {
            // Pode ser cliente não encontrado ou validação de negócio
            if (e.getMessage().contains("não encontrado")) {
                return ResponseEntity
                        .status(404)
                        .body(ApiResponse.fail(e.getMessage()));
            }
            // Erros de validação (email duplicado, CPF duplicado)
            return ResponseEntity
                    .status(422)
                    .body(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(ApiResponse.fail("Erro ao atualizar perfil: " + e.getMessage()));
        }
    }
}
