package com.ecommerce.producer.service;

import com.ecommerce.producer.dto.OrderRequest;
import com.ecommerce.producer.dto.OrderUpdateRequest;
import com.ecommerce.producer.model.Item;
import com.ecommerce.producer.model.Order;
import com.ecommerce.producer.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
@Service
public class OrderService {

    private final Random random = new Random();
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // Existing method to create an order (for example, via /create-order)
    public Order createOrder(OrderRequest request) {
        Order order = new Order();
        order.setOrderId(request.getOrderId());
        order.setCustomerId("CUST-" + (10000 + random.nextInt(90000)));
        order.setOrderDate(Instant.now());

        // Generate a simple list of items (this logic can be more complex as needed)
        List<Item> items = new ArrayList<>();
        Item item = new Item();
        item.setItemId("ITEM-001");
        item.setQuantity(1);
        item.setPrice(19.99);
        items.add(item);
        order.setItems(items);

        order.setTotalAmount(19.99);
        order.setCurrency("USD");
        order.setStatus("new");

        // Save the newly created order in the repository
        orderRepository.save(order);
        return order;
    }

    // New method to update an existing order's status
    public Order updateOrder(OrderUpdateRequest updateRequest) {
        Order order = orderRepository.findByOrderId(updateRequest.getOrderId());
        if (order != null) {
            order.setStatus(updateRequest.getStatus());
            // Save the updated order back to the repository
            orderRepository.save(order);
        }
        return order;
    }
}

