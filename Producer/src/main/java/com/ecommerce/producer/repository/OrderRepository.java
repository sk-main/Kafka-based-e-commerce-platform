package com.ecommerce.producer.repository;

import com.ecommerce.producer.model.Order;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class OrderRepository {

    private final Map<String, Order> orderStore = new ConcurrentHashMap<>();

    public Order save(Order order) {
        orderStore.put(order.getOrderId(), order);
        return order;
    }

    public Order findByOrderId(String orderId) {
        return orderStore.get(orderId);
    }
}

