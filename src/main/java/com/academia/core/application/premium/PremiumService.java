package com.academia.core.application.premium;

import com.academia.core.application.clientes.ClienteService;
import com.academia.core.application.pagamentos.PagamentoService;
import com.academia.core.config.AbacatePayProperties;
import com.academia.core.domain.clientes.Cliente;
import com.academia.core.domain.pagamentos.MetodoPagamento;
import com.academia.core.domain.pagamentos.Pagamento;
import com.academia.core.domain.pagamentos.StatusPagamento;
import com.academia.core.infrastructure.abacatepay.AbacatePayClient;
import com.academia.core.infrastructure.abacatepay.dto.*;
import com.academia.core.interfaces.premium.dto.PremiumCheckoutResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PremiumService {

    private static final Logger log = LoggerFactory.getLogger(PremiumService.class);

    private static final int PREMIUM_VALUE_IN_CENTS = 100;
    private static final BigDecimal PREMIUM_VALUE = BigDecimal.valueOf(PREMIUM_VALUE_IN_CENTS, 2);
    private static final String PREMIUM_PRODUCT_EXTERNAL_ID = "premium-1m";

    private final AbacatePayClient abacatePayClient;
    private final ClienteService clienteService;
    private final PagamentoService pagamentoService;
    private final AbacatePayProperties properties;

    public PremiumService(AbacatePayClient abacatePayClient,
                          ClienteService clienteService,
                          PagamentoService pagamentoService,
                          AbacatePayProperties properties) {
        this.abacatePayClient = abacatePayClient;
        this.clienteService = clienteService;
        this.pagamentoService = pagamentoService;
        this.properties = properties;
    }

    public PremiumCheckoutResponse iniciarCheckout(Cliente cliente) {
        validarDadosCliente(cliente);

        AbacatePayBillingRequest request = buildBillingRequest(cliente);
        AbacatePayBillingResponseData cobranca = abacatePayClient.criarCobranca(request);

        Pagamento pagamento = Pagamento.builder()
                .clienteId(cliente.getId())
                .valor(PREMIUM_VALUE)
                .metodo(MetodoPagamento.PIX)
                .status(StatusPagamento.PENDENTE)
                .referenciaExterna(cobranca.getId())
                .build();

        pagamentoService.registrarPagamento(pagamento);

        PremiumCheckoutResponse response = new PremiumCheckoutResponse();
        response.setBillingId(cobranca.getId());
        response.setCheckoutUrl(cobranca.getUrl());
        response.setAmount(PREMIUM_VALUE);
        return response;
    }

    public void processarWebhook(String signature, AbacatePayWebhookPayload payload) {
        if (!abacatePayClient.isValidWebhookSignature(signature)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Assinatura do webhook inválida");
        }

        if (payload == null || payload.getData() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payload de webhook inválido");
        }

        String evento = payload.getEvent();
        if (!"billing.paid".equalsIgnoreCase(evento)) {
            log.info("Ignorando evento do webhook: {}", evento);
            return;
        }

        String referencia = payload.getData().getId();
        if (referencia == null || referencia.isBlank()) {
            log.warn("Webhook billing.paid sem id de cobrança");
            return;
        }

        Pagamento pagamento = pagamentoService.buscarPorReferenciaExterna(referencia)
                .orElse(null);

        if (pagamento == null) {
            log.warn("Pagamento com referência {} não encontrado. Ignorando webhook.", referencia);
            return;
        }

        if (StatusPagamento.APROVADO.equals(pagamento.getStatus())) {
            log.info("Pagamento {} já estava aprovado. Ignorando.", referencia);
            return;
        }

        pagamento.setStatus(StatusPagamento.APROVADO);
        pagamentoService.atualizarPagamento(pagamento);

        LocalDate dataBase = clienteService.buscarPorId(pagamento.getClienteId())
                .map(cliente -> {
                    LocalDate hoje = LocalDate.now();
                    LocalDate atual = cliente.getPremiumAte();
                    if (atual != null && atual.isAfter(hoje)) {
                        return atual;
                    }
                    return hoje;
                })
                .orElse(LocalDate.now());

        clienteService.atualizarStatusPremium(
                pagamento.getClienteId(),
                true,
                dataBase.plusMonths(1)
        );

        log.info("Cliente {} marcado como premium após pagamento {}", pagamento.getClienteId(), referencia);
    }

    private void validarDadosCliente(Cliente cliente) {
        if (cliente.getCpf() == null || cliente.getCpf().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Atualize seu CPF no perfil para assinar o plano premium");
        }

        if (cliente.getTelefone() == null || cliente.getTelefone().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Atualize seu telefone no perfil para assinar o plano premium");
        }
    }

    private AbacatePayBillingRequest buildBillingRequest(Cliente cliente) {
        AbacatePayBillingProduct produto = new AbacatePayBillingProduct();
        produto.setExternalId(PREMIUM_PRODUCT_EXTERNAL_ID);
        produto.setName("Premium 1 mês");
        produto.setDescription("Acesso premium por 30 dias");
        produto.setQuantity(1);
        produto.setPrice(PREMIUM_VALUE_IN_CENTS);

        AbacatePayBillingCustomer customer = new AbacatePayBillingCustomer();
        customer.setName(cliente.getNome());
        customer.setCellphone(onlyDigits(cliente.getTelefone()));
        customer.setEmail(cliente.getEmail());
        customer.setTaxId(onlyDigits(cliente.getCpf()));

        AbacatePayBillingRequest request = new AbacatePayBillingRequest();
        request.setFrequency("ONE_TIME");
        request.setMethods(List.of("PIX"));
        request.setProducts(List.of(produto));
        request.setReturnUrl(properties.getReturnUrl());
        request.setCompletionUrl(properties.getCompletionUrl());
        request.setCustomer(customer);
        request.setMetadata(buildMetadata(cliente));
        return request;
    }

    private Map<String, Object> buildMetadata(Cliente cliente) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("clienteId", cliente.getId());
        metadata.put("plano", PREMIUM_PRODUCT_EXTERNAL_ID);
        return metadata;
    }

    private String onlyDigits(String value) {
        return value == null ? null : value.replaceAll("\\D", "");
    }
}
