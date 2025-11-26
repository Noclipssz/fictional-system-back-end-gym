package com.academia.core.infrastructure.abacatepay.dto;

public class AbacatePayWebhookPayload {

    private String event;
    private AbacatePayWebhookData data;

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public AbacatePayWebhookData getData() {
        return data;
    }

    public void setData(AbacatePayWebhookData data) {
        this.data = data;
    }
}
