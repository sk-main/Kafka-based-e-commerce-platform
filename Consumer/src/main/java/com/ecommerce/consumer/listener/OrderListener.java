package com.ecommerce.consumer.listener;

import com.ecommerce.consumer.model.Order;
import com.ecommerce.consumer.repository.OrderRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderListener {

    private final OrderRepository orderRepository;

    public OrderListener(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @KafkaListener(topics = "orders", groupId = "order-consumers")
    public void listen(Order order) {
        // Check for duplicate processing (if an order with the same orderId exists, skip processing).
        if (orderRepository.findByOrderId(order.getOrderId()) != null) {
            System.out.printf("Order with ID: %s is already processed. Skipping processing.%n", order.getOrderId());
            return;
        }

        // Calculate shipping cost (2% of totalAmount) and set it.
        double shippingCost = order.getTotalAmount() * 0.02;
        order.setShippingCost(shippingCost);

        // Log the order details.
        System.out.printf("Processing Order [ID: %s]: Total Amount=%.2f, Shipping Cost=%.2f%n",
                order.getOrderId(), order.getTotalAmount(), shippingCost);

        // Save the processed order in the repository.
        orderRepository.save(order);
    }
}



