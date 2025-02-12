package com.ecommerce.consumer.controller;

import com.example.avro.Order;
import com.ecommerce.consumer.repository.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping("/order-details")
    public ResponseEntity<?> getOrderDetails(@RequestParam String orderId) {
        Order order = orderRepository.findByOrderId(orderId);
        if (order == null) {
            return new ResponseEntity<>("Order not found", HttpStatus.NOT_FOUND);
        }

        try {
            // Convert Avro object to Map to avoid serialization issues
            Map<String, Object> response = new HashMap<>();
            response.put("orderId", order.getOrderId().toString());  // Convert CharSequence to String
            response.put("customerId", order.getCustomerId().toString());
            response.put("orderDate", order.getOrderDate());
            response.put("totalAmount", order.getTotalAmount());
            response.put("currency", order.getCurrency().toString());
            response.put("status", order.getStatus().toString());

            // Convert items list, properly handling CharSequence fields
            response.put("items", order.getItems().stream()
                    .map(item -> Map.of(
                            "itemId", item.getItemId().toString(),  // Convert CharSequence to String
                            "quantity", item.getQuantity(),
                            "price", item.getPrice()
                    ))
                    .collect(Collectors.toList()));

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error processing order details: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getAllOrderIdsFromTopic")
    public ResponseEntity<?> getAllOrderIdsFromTopic(@RequestParam String topic) {
        if (!"orders".equals(topic)) {
            return new ResponseEntity<>("Topic not subscribed.", HttpStatus.NOT_FOUND);
        }

        Set<String> orderIds = orderRepository.getAllOrderIds();
        return new ResponseEntity<>(orderIds, HttpStatus.OK);
    }
}

