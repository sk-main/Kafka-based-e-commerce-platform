package com.ecommerce.consumer.repository;

import com.ecommerce.consumer.model.Order;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class OrderRepository {
    // In-memory storage keyed by orderId
    private final Map<String, Order> orderStore = new ConcurrentHashMap<>();

    public void save(Order order) {
        orderStore.put(order.getOrderId(), order);
    }

    public Order findByOrderId(String orderId) {
        return orderStore.get(orderId);
    }

    public Set<String> getAllOrderIds() {
        return orderStore.keySet();
    }
}

