package com.academia.core.domain.pagamentos;

import java.util.List;
import java.util.Optional;

public interface PagamentoRepositoryPort {
    Pagamento save(Pagamento p);
    List<Pagamento> findAll();
    Optional<Pagamento> findById(Long id);
    List<Pagamento> findByClienteId(Long clienteId);
}
