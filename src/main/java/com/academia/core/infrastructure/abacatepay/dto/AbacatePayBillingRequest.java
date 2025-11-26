package com.academia.core.infrastructure.abacatepay.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AbacatePayBillingRequest {

    private String frequency;
    private List<String> methods;
    private List<AbacatePayBillingProduct> products;
    private String returnUrl;
    private String completionUrl;
    private String customerId;
    private AbacatePayBillingCustomer customer;
    private Map<String, Object> metadata;

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public List<String> getMethods() {
        return methods;
    }

    public void setMethods(List<String> methods) {
        this.methods = methods;
    }

    public List<AbacatePayBillingProduct> getProducts() {
        return products;
    }

    public void setProducts(List<AbacatePayBillingProduct> products) {
        this.products = products;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getCompletionUrl() {
        return completionUrl;
    }

    public void setCompletionUrl(String completionUrl) {
        this.completionUrl = completionUrl;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public AbacatePayBillingCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(AbacatePayBillingCustomer customer) {
        this.customer = customer;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}
