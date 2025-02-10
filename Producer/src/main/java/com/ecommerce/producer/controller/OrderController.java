package com.ecommerce.producer.controller;

import com.ecommerce.producer.config.KafkaProducerConfig;
import com.ecommerce.producer.dto.OrderRequest;
import com.ecommerce.producer.dto.OrderUpdateRequest;
import com.ecommerce.producer.model.Order;
import com.ecommerce.producer.service.OrderService;
import org.apache.kafka.common.KafkaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api")
@Validated
public class OrderController {

    private final OrderService orderService;
    private final KafkaTemplate<String, Order> kafkaTemplate;

    public OrderController(OrderService orderService, KafkaTemplate<String, Order> kafkaTemplate) {
        this.orderService = orderService;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * POST /api/create-order
     * Creates a new order and publishes it to Kafka.
     */
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        // Create the order (with business logic)
        Order order = orderService.createOrder(orderRequest);

        try {
            // Synchronously send the order to Kafka
            kafkaTemplate.send(KafkaProducerConfig.ORDER_TOPIC, order.getOrderId(), order).get();
        } catch (InterruptedException e) {
            // Restore interrupted state...
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Operation interrupted. Please try again later.");
        } catch (ExecutionException e) {
            // Kafka send failed, which could be due to connection issues or broker unavailability.
            Throwable cause = e.getCause();
            // Log the error as needed (omitted here for brevity)
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Failed to send order to Kafka: " + cause.getMessage());
        } catch (KafkaException ke) {
            // Catch any other Kafka-specific exceptions.
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Kafka error: " + ke.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    /**
     * PUT /api/update-order
     * Updates an existing order's status and publishes the updated order to Kafka.
     */
    @PutMapping("/update-order")
    public ResponseEntity<?> updateOrder(@Valid @RequestBody OrderUpdateRequest updateRequest) {
        // Update the order's status
        Order order = orderService.updateOrder(updateRequest);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found.");
        }

        try {
            // Synchronously send the updated order to Kafka
            kafkaTemplate.send(KafkaProducerConfig.ORDER_TOPIC, order.getOrderId(), order).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Operation interrupted. Please try again later.");
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Failed to update order in Kafka: " + cause.getMessage());
        } catch (KafkaException ke) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Kafka error: " + ke.getMessage());
        }

        return ResponseEntity.ok(order);
    }
}


