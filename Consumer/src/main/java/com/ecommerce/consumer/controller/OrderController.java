package com.ecommerce.consumer.controller;

import com.ecommerce.consumer.model.Order;
import com.ecommerce.consumer.repository.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // Existing endpoint to get order details by orderId.
    @GetMapping("/order-details")
    public ResponseEntity<?> getOrderDetails(@RequestParam String orderId) {
        Order order = orderRepository.findByOrderId(orderId);
        if (order != null) {
            return new ResponseEntity<>(order, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Order not found", HttpStatus.NOT_FOUND);
        }
    }

    // New endpoint to retrieve all order IDs from a specified topic.
    @GetMapping("/getAllOrderIdsFromTopic")
    public ResponseEntity<?> getAllOrderIdsFromTopic(@RequestParam String topic) {
        // In this example, our consumer only subscribes to the "orders" topic.
        if (!"orders".equals(topic)) {
            return new ResponseEntity<>("Topic not subscribed.", HttpStatus.NOT_FOUND);
        }

        Set<String> orderIds = orderRepository.getAllOrderIds();
        return new ResponseEntity<>(orderIds, HttpStatus.OK);
    }
}

