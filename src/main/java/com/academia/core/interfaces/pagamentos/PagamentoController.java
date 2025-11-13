package com.academia.core.interfaces.pagamentos;

import com.academia.core.common.ApiResponse;
import com.academia.core.application.pagamentos.PagamentoService;
import com.academia.core.domain.pagamentos.Pagamento;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> listar() {
        return ResponseEntity.ok(
                ApiResponse.ok(pagamentoService.listarPagamentos(), "Lista de pagamentos")
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Pagamento>> buscarPorId(@PathVariable("id") Long id) {
        return pagamentoService.buscarPorId(id)
                .map(p -> ResponseEntity.ok(ApiResponse.ok(p, "Pagamento encontrado")))
                .orElseGet(() -> ResponseEntity.status(404).body(ApiResponse.fail("Pagamento não encontrado")));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<ApiResponse<?>> listarPorCliente(@PathVariable("clienteId") Long clienteId) {
        return ResponseEntity.ok(
                ApiResponse.ok(pagamentoService.listarPorCliente(clienteId), "Pagamentos do cliente")
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Pagamento>> registrar(@Valid @RequestBody Pagamento pagamento) {
        Pagamento salvo = pagamentoService.registrarPagamento(pagamento);
        return ResponseEntity.status(201).body(ApiResponse.ok(salvo, "Pagamento registrado com sucesso"));
    }
}
