package com.ecommerce.producer.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class OrderRequest {

    @NotBlank(message = "orderId must not be blank")
    @Pattern(regexp = "^ORD-\\d+$", message = "orderId must follow the pattern ORD-<digits>")
    private String orderId;

    @Min(value = 1, message = "itemsNum must be at least 1")
    private int itemsNum;

    // Getters and Setters

    public String getOrderId() {
        return orderId;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public int getItemsNum() {
        return itemsNum;
    }
    public void setItemsNum(int itemsNum) {
        this.itemsNum = itemsNum;
    }
}

