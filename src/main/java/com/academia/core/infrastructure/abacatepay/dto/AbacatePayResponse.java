package com.academia.core.infrastructure.abacatepay.dto;

public class AbacatePayResponse<T> {

    private T data;
    private Object error;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }
}
