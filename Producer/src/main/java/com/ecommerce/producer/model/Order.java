package com.ecommerce.producer.model;

import java.time.Instant;
import java.util.List;

public class Order {
    private String orderId;
    private String customerId;
    private Instant orderDate;
    private List<Item> items;
    private double totalAmount;
    private String currency;
    private String status;

    // Getters and Setters

    public String getOrderId() {
        return orderId;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public String getCustomerId() {
        return customerId;
    }
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    public Instant getOrderDate() {
        return orderDate;
    }
    public void setOrderDate(Instant orderDate) {
        this.orderDate = orderDate;
    }
    public List<Item> getItems() {
        return items;
    }
    public void setItems(List<Item> items) {
        this.items = items;
    }
    public double getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}

