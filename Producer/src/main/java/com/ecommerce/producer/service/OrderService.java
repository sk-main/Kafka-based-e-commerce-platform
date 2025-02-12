package com.ecommerce.producer.service;

import com.ecommerce.producer.dto.OrderRequest;
import com.ecommerce.producer.dto.OrderUpdateRequest;
import com.example.avro.Item;
import com.example.avro.Order;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class OrderService {

    private final KafkaTemplate<String, Order> kafkaTemplate;
    // In-memory store for orders (for demonstration purposes)
    private final ConcurrentMap<String, Order> orderStore = new ConcurrentHashMap<>();

    public OrderService(KafkaTemplate<String, Order> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public Order createOrder(OrderRequest request) {
        Order order = new Order();
        order.setOrderId(request.getOrderId());
        order.setCustomerId("CUST-" + (10000 + new Random().nextInt(90000)));
        // Set order date as an Instant
        order.setOrderDate(Instant.now());

        // Create dummy items based on itemsNum from the request
        List<Item> items = new ArrayList<>();
        for (int i = 1; i <= request.getItemsNum(); i++) {
            Item item = new Item();
            item.setItemId(String.format("ITEM-%03d", i));
            item.setQuantity(1 + new Random().nextInt(5));
            double price = Math.round((10 + (90 * new Random().nextDouble())) * 100.0) / 100.0;
            item.setPrice(price);
            items.add(item);
        }
        order.setItems(items);

        double total = items.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
        order.setTotalAmount(Math.round(total * 100.0) / 100.0);
        order.setCurrency("USD");
        order.setStatus("new");

        // Save the order in our in-memory store
        orderStore.put(order.getOrderId().toString(), order);

        // Publish the order to Kafka using CompletableFuture-style callbacks.
        // If your KafkaTemplate.send() returns a CompletableFuture, use thenAccept() and exceptionally().
        kafkaTemplate.send("orders", order.getOrderId().toString(), order)
                // If your version returns a ListenableFuture, you can convert it to a CompletableFuture:
                //.completable()
                .thenAccept((SendResult<String, Order> result) -> {
                    System.out.printf("Order '%s' created successfully. Partition: %d, Offset: %d%n",
                            order.getOrderId(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                })
                .exceptionally(ex -> {
                    System.err.printf("Failed to send order '%s': %s%n", order.getOrderId(), ex.getMessage());
                    ex.printStackTrace();
                    return null;
                });

        return order;
    }

    public Order updateOrder(OrderUpdateRequest request) {
        Order order = orderStore.get(request.getOrderId());
        if (order != null) {
            order.setStatus(request.getStatus());
            orderStore.put(order.getOrderId().toString(), order);

            kafkaTemplate.send("orders", order.getOrderId().toString(), order)
                    //.completable()  // Uncomment if needed.
                    .thenAccept((SendResult<String, Order> result) -> {
                        System.out.printf("Order '%s' updated successfully. Partition: %d, Offset: %d%n",
                                order.getOrderId(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    })
                    .exceptionally(ex -> {
                        System.err.printf("Failed to update order '%s': %s%n", order.getOrderId(), ex.getMessage());
                        ex.printStackTrace();
                        return null;
                    });
        }
        return order;
    }
}



