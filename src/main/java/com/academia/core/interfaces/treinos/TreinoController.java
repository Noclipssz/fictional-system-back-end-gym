package com.academia.core.interfaces.treinos;

import com.academia.core.common.ApiResponse;
import com.academia.core.application.treinos.TreinoService;
import com.academia.core.domain.treinos.Treino;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/treinos")
public class TreinoController {

    private final TreinoService treinoService;

    public TreinoController(TreinoService treinoService) {
        this.treinoService = treinoService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> listar() {
        return ResponseEntity.ok(
                ApiResponse.ok(treinoService.listarTreinos(), "Lista de treinos")
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Treino>> buscarPorId(@PathVariable("id") Long id) {
        return treinoService.buscarPorId(id)
                .map(treino -> ResponseEntity.ok(ApiResponse.ok(treino, "Treino encontrado")))
                .orElseGet(() -> ResponseEntity.status(404).body(ApiResponse.fail("Treino não encontrado")));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<ApiResponse<?>> listarPorCliente(@PathVariable("clienteId") Long clienteId) {
        return ResponseEntity.ok(
                ApiResponse.ok(treinoService.listarPorCliente(clienteId), "Treinos do cliente")
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Treino>> criar(@Valid @RequestBody Treino treino) {
        Treino salvo = treinoService.criarTreino(treino);
        return ResponseEntity.status(201).body(ApiResponse.ok(salvo, "Treino criado com sucesso"));
    }
}
