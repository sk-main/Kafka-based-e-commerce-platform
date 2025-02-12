package com.ecommerce.producer.controller;

import com.ecommerce.producer.dto.OrderRequest;
import com.ecommerce.producer.dto.OrderUpdateRequest;
import com.example.avro.Order;
import com.ecommerce.producer.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@Validated
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        try {
            Order order = orderService.createOrder(orderRequest);

            // Create a response DTO to avoid serialization issues with Avro objects
            Map<String, Object> response = new HashMap<>();
            response.put("orderId", order.getOrderId());
            response.put("customerId", order.getCustomerId());
            response.put("status", order.getStatus());
            response.put("totalAmount", order.getTotalAmount());
            response.put("orderDate", order.getOrderDate());
            response.put("items", order.getItems().stream()
                    .map(item -> Map.of(
                            "itemId", item.getItemId(),
                            "quantity", item.getQuantity(),
                            "price", item.getPrice()
                    ))
                    .collect(Collectors.toList()));
            response.put("currency", order.getCurrency());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating order: " + e.getMessage());
        }
    }

    @PutMapping("/update-order")
    public ResponseEntity<?> updateOrder(@Valid @RequestBody OrderUpdateRequest updateRequest) {
        try {
            Order order = orderService.updateOrder(updateRequest);
            if (order == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Order not found");
            }

            // Create a response DTO
            Map<String, Object> response = new HashMap<>();
            response.put("orderId", order.getOrderId());
            response.put("customerId", order.getCustomerId());
            response.put("status", order.getStatus());
            response.put("totalAmount", order.getTotalAmount());
            response.put("orderDate", order.getOrderDate());
            response.put("items", order.getItems().stream()
                    .map(item -> Map.of(
                            "itemId", item.getItemId(),
                            "quantity", item.getQuantity(),
                            "price", item.getPrice()
                    ))
                    .collect(Collectors.toList()));
            response.put("currency", order.getCurrency());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating order: " + e.getMessage());
        }
    }
}


