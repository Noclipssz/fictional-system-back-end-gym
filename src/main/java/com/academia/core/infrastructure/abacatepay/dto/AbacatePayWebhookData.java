package com.academia.core.infrastructure.abacatepay.dto;

public class AbacatePayWebhookData {

    private String id;
    private String status;
    private Integer amount;
    private AbacatePayWebhookCustomer customer;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public AbacatePayWebhookCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(AbacatePayWebhookCustomer customer) {
        this.customer = customer;
    }
}
