package com.academia.core.interfaces.premium;

import com.academia.core.application.auth.AuthService;
import com.academia.core.application.premium.PremiumService;
import com.academia.core.common.ApiResponse;
import com.academia.core.domain.clientes.Cliente;
import com.academia.core.infrastructure.abacatepay.dto.AbacatePayWebhookPayload;
import com.academia.core.interfaces.premium.dto.PremiumCheckoutResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/premium")
public class PremiumController {

    private final PremiumService premiumService;
    private final AuthService authService;

    public PremiumController(PremiumService premiumService, AuthService authService) {
        this.premiumService = premiumService;
        this.authService = authService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<PremiumCheckoutResponse>> criarCheckout() {
        Cliente current = authService.getCurrentUser();
        PremiumCheckoutResponse response = premiumService.iniciarCheckout(current);
        return ResponseEntity.ok(ApiResponse.ok(response, "Checkout criado com sucesso"));
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> receberWebhook(
            @RequestHeader(name = "X-Webhook-Signature", required = false) String signature,
            @RequestParam(name = "webhookSecret", required = false) String secretParam,
            @RequestBody AbacatePayWebhookPayload payload
    ) {
        String providedSignature = signature != null ? signature : secretParam;
        premiumService.processarWebhook(providedSignature, payload);
        return ResponseEntity.ok().build();
    }
}
