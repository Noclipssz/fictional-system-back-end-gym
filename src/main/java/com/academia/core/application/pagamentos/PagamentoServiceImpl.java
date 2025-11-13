package com.academia.core.application.pagamentos;

import com.academia.core.domain.pagamentos.Pagamento;
import com.academia.core.domain.pagamentos.PagamentoRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class PagamentoServiceImpl implements PagamentoService {

    private final PagamentoRepositoryPort pagamentoRepository;

    public PagamentoServiceImpl(PagamentoRepositoryPort pagamentoRepository) {
        this.pagamentoRepository = pagamentoRepository;
    }

    @Override
    public Pagamento registrarPagamento(Pagamento pagamento) {
        pagamento.setCreatedAt(Instant.now());
        return pagamentoRepository.save(pagamento);
    }

    @Override
    public List<Pagamento> listarPagamentos() {
        return pagamentoRepository.findAll();
    }

    @Override
    public Optional<Pagamento> buscarPorId(Long id) {
        return pagamentoRepository.findById(id);
    }

    @Override
    public List<Pagamento> listarPorCliente(Long clienteId) {
        return pagamentoRepository.findByClienteId(clienteId);
    }
}
