package com.academia.core.infrastructure.abacatepay;

import com.academia.core.config.AbacatePayProperties;
import com.academia.core.infrastructure.abacatepay.dto.AbacatePayBillingRequest;
import com.academia.core.infrastructure.abacatepay.dto.AbacatePayBillingResponseData;
import com.academia.core.infrastructure.abacatepay.dto.AbacatePayResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class AbacatePayClient {

    private static final Logger log = LoggerFactory.getLogger(AbacatePayClient.class);

    private final RestTemplate restTemplate;
    private final AbacatePayProperties properties;

    public AbacatePayClient(RestTemplate restTemplate, AbacatePayProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    public AbacatePayBillingResponseData criarCobranca(AbacatePayBillingRequest request) {
        try {
            HttpHeaders headers = buildHeaders();
            HttpEntity<AbacatePayBillingRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<AbacatePayResponse<AbacatePayBillingResponseData>> response = restTemplate.exchange(
                    buildUrl("/billing/create"),
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new IllegalStateException("AbacatePay retornou status " + response.getStatusCode());
            }

            AbacatePayResponse<AbacatePayBillingResponseData> body = response.getBody();

            if (body == null || body.getData() == null) {
                throw new IllegalStateException("Resposta da AbacatePay sem corpo ou dados");
            }

            return body.getData();
        } catch (RestClientException ex) {
            log.error("Erro ao chamar AbacatePay", ex);
            throw ex;
        }
    }

    public boolean isValidWebhookSignature(String providedSignature) {
        String expected = properties.getWebhookSecret();
        return expected != null && !expected.isBlank() && expected.equals(providedSignature);
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String apiKey = properties.getApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Chave de API da AbacatePay não configurada");
        }
        headers.setBearerAuth(apiKey);
        return headers;
    }

    private String buildUrl(String path) {
        String base = properties.getBaseUrl();
        if (base == null || base.isBlank()) {
            throw new IllegalStateException("URL base da AbacatePay não configurada");
        }
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return base + path;
    }
}
