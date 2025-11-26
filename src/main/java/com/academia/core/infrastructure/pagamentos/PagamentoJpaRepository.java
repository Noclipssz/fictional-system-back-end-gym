package com.academia.core.infrastructure.pagamentos;

import com.academia.core.domain.pagamentos.Pagamento;
import com.academia.core.domain.pagamentos.PagamentoRepositoryPort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PagamentoJpaRepository extends JpaRepository<Pagamento, Long>, PagamentoRepositoryPort {

    @Override
    Pagamento save(Pagamento p);

    @Override
    List<Pagamento> findAll();

    List<Pagamento> findByClienteId(Long clienteId);

    Optional<Pagamento> findByReferenciaExterna(String referenciaExterna);
}
