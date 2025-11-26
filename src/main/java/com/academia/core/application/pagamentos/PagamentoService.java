package com.academia.core.application.pagamentos;

import com.academia.core.domain.pagamentos.Pagamento;

import java.util.List;
import java.util.Optional;

public interface PagamentoService {

    Pagamento registrarPagamento(Pagamento pagamento);

    List<Pagamento> listarPagamentos();

    Optional<Pagamento> buscarPorId(Long id);

    List<Pagamento> listarPorCliente(Long clienteId);

    Optional<Pagamento> buscarPorReferenciaExterna(String referenciaExterna);

    Pagamento atualizarPagamento(Pagamento pagamento);
}
