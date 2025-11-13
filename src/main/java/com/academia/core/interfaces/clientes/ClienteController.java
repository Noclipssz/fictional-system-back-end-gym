package com.academia.core.interfaces.clientes;

import com.academia.core.application.clientes.ClienteService;
import com.academia.core.domain.clientes.Cliente;
import com.academia.core.interfaces.clientes.dto.ClienteResponseDto;
import com.academia.core.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> listar() {
        List<ClienteResponseDto> lista = clienteService.listarClientes()
                .stream()
                .map(ClienteMapper::toResponseDto)
                .toList();

        return ResponseEntity.ok(
                ApiResponse.ok(lista, "Lista de clientes")
        );
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

    @PostMapping
    public ResponseEntity<ApiResponse<?>> criar(@Valid @RequestBody Cliente cliente) {
        Cliente salvo = clienteService.criarCliente(cliente);
        ClienteResponseDto dto = ClienteMapper.toResponseDto(salvo);

        return ResponseEntity
                .status(201)
                .body(ApiResponse.ok(dto, "Cliente criado com sucesso"));
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
