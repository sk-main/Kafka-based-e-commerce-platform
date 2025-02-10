package com.ecommerce.producer.dto;

import jakarta.validation.constraints.NotBlank;

public class OrderUpdateRequest {

    @NotBlank(message = "orderId must not be blank")
    private String orderId;

    @NotBlank(message = "status must not be blank")
    private String status;

    // Getters and Setters
    public String getOrderId() {
        return orderId;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}

