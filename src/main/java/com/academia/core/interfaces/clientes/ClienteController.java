package com.academia.core.interfaces.clientes;

import com.academia.core.application.auth.AuthService;
import com.academia.core.application.clientes.ClienteService;
import com.academia.core.domain.clientes.Cliente;
import com.academia.core.interfaces.clientes.dto.ClienteResponseDto;
import com.academia.core.interfaces.clientes.dto.AlterarSenhaRequest;
import com.academia.core.interfaces.clientes.dto.ExcluirContaRequest;
import com.academia.core.common.ApiResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private static final Logger log = LoggerFactory.getLogger(ClienteController.class);

    private final ClienteService clienteService;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    public ClienteController(ClienteService clienteService, AuthService authService, PasswordEncoder passwordEncoder) {
        this.clienteService = clienteService;
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
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

    /**
     * Endpoint para ativar/desativar premium (apenas para desenvolvimento/testes)
     * Em produção, isso deve ser feito apenas via webhook de pagamento
     */
    @PostMapping("/{id}/premium")
    public ResponseEntity<ApiResponse<?>> ativarPremium(
            @PathVariable("id") Long id,
            @RequestParam(name = "ativar", defaultValue = "true") boolean ativar,
            @RequestParam(name = "meses", defaultValue = "1") int meses
    ) {
        try {
            log.info("Ativando premium para cliente id={}, ativar={}, meses={}", id, ativar, meses);

            LocalDate premiumAte = ativar ? LocalDate.now().plusMonths(meses) : null;
            Cliente atualizado = clienteService.atualizarStatusPremium(id, ativar, premiumAte);
            ClienteResponseDto dto = ClienteMapper.toResponseDto(atualizado);

            String msg = ativar
                    ? "Premium ativado até " + premiumAte
                    : "Premium desativado";

            return ResponseEntity.ok(ApiResponse.ok(dto, msg));
        } catch (Exception e) {
            log.error("Erro ao ativar premium: {}", e.getMessage());
            return ResponseEntity
                    .status(500)
                    .body(ApiResponse.fail("Erro ao ativar premium: " + e.getMessage()));
        }
    }

    /**
     * Endpoint para alterar senha do cliente
     */
    @PutMapping("/{id}/senha")
    public ResponseEntity<ApiResponse<?>> alterarSenha(
            @PathVariable("id") Long id,
            @Valid @RequestBody AlterarSenhaRequest request
    ) {
        try {
            log.info("Tentativa de alterar senha: id={}", id);

            // Validar que o usuário está alterando apenas a própria senha
            Cliente currentUser = authService.getCurrentUser();
            log.info("Usuário autenticado: id={}, username={}",
                    currentUser.getId(), currentUser.getUsername());

            if (!currentUser.getId().equals(id)) {
                log.warn("Tentativa de alterar senha de outro usuário. Current={}, Target={}",
                        currentUser.getId(), id);
                return ResponseEntity
                        .status(403)
                        .body(ApiResponse.fail("Você só pode alterar sua própria senha"));
            }

            // Verificar senha atual
            if (!passwordEncoder.matches(request.getSenhaAtual(), currentUser.getSenha())) {
                log.warn("Senha atual incorreta para usuário id={}", id);
                return ResponseEntity
                        .status(400)
                        .body(ApiResponse.fail("Senha atual incorreta"));
            }

            // Atualizar senha
            String novaSenhaEncoded = passwordEncoder.encode(request.getNovaSenha());
            clienteService.atualizarSenha(id, novaSenhaEncoded);

            log.info("Senha alterada com sucesso para id={}", id);

            return ResponseEntity.ok(ApiResponse.ok(null, "Senha alterada com sucesso"));
        } catch (Exception e) {
            log.error("Erro ao alterar senha: {}", e.getMessage());
            return ResponseEntity
                    .status(500)
                    .body(ApiResponse.fail("Erro ao alterar senha: " + e.getMessage()));
        }
    }

    /**
     * Endpoint para excluir conta do cliente
     * Requer confirmação de senha
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> excluirConta(
            @PathVariable("id") Long id,
            @Valid @RequestBody ExcluirContaRequest request
    ) {
        try {
            log.info("Tentativa de excluir conta: id={}", id);

            // Validar que o usuário está excluindo apenas a própria conta
            Cliente currentUser = authService.getCurrentUser();
            log.info("Usuário autenticado: id={}, username={}",
                    currentUser.getId(), currentUser.getUsername());

            if (!currentUser.getId().equals(id)) {
                log.warn("Tentativa de excluir conta de outro usuário. Current={}, Target={}",
                        currentUser.getId(), id);
                return ResponseEntity
                        .status(403)
                        .body(ApiResponse.fail("Você só pode excluir sua própria conta"));
            }

            // Verificar senha
            if (!passwordEncoder.matches(request.getSenha(), currentUser.getSenha())) {
                log.warn("Senha incorreta para exclusão de conta id={}", id);
                return ResponseEntity
                        .status(400)
                        .body(ApiResponse.fail("Senha incorreta"));
            }

            // Excluir conta
            clienteService.excluirCliente(id);

            log.info("Conta excluída com sucesso para id={}", id);

            return ResponseEntity.ok(ApiResponse.ok(null, "Conta excluída com sucesso"));
        } catch (IllegalArgumentException e) {
            log.error("Erro ao excluir conta: {}", e.getMessage());
            return ResponseEntity
                    .status(404)
                    .body(ApiResponse.fail(e.getMessage()));
        } catch (Exception e) {
            log.error("Erro ao excluir conta: {}", e.getMessage());
            return ResponseEntity
                    .status(500)
                    .body(ApiResponse.fail("Erro ao excluir conta: " + e.getMessage()));
        }
    }
}
