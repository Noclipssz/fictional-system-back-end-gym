package com.academia.core.interfaces.bff;

import com.academia.core.application.clientes.ClienteService;
import com.academia.core.common.ApiResponse;
import com.academia.core.domain.clientes.Cliente;
import com.academia.core.interfaces.clientes.ClienteMapper;
import com.academia.core.interfaces.clientes.dto.ClienteResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bff/clientes")
@CrossOrigin(origins = "*")
public class BffClienteController {

    private final ClienteService clienteService;

    public BffClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteResponseDto>> buscarPorId(@PathVariable("id") Long id) {
        return clienteService.buscarPorId(id)
                .map(cliente -> {
                    ClienteResponseDto dto = ClienteMapper.toResponseDto(cliente);
                    return ResponseEntity.ok(
                            ApiResponse.ok(dto, "Cliente encontrado")
                    );
                })
                .orElseGet(() ->
                        ResponseEntity.status(404)
                                .body(ApiResponse.fail("Cliente não encontrado"))
                );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> atualizar(
            @PathVariable("id") Long id,
            @Valid @RequestBody Cliente body
    ) {
        try {
            Cliente atualizado = clienteService.atualizarCliente(id, body);
            ClienteResponseDto dto = ClienteMapper.toResponseDto(atualizado);

            return ResponseEntity.ok(
                    ApiResponse.ok(dto, "Cliente atualizado com sucesso")
            );
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(404)
                    .body(ApiResponse.fail(e.getMessage()));
        }
    }
}
