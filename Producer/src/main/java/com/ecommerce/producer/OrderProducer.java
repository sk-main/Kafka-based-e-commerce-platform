package com.ecommerce.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class OrderProducer {

    public static void main(String[] args) {
        // Kafka Producer configuration
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");  // Adjust as necessary
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        // (Optional) Enable idempotence for exactly-once semantics if needed:
        // props.put("enable.idempotence", "true");

        // Create the KafkaProducer
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        // The topic where order events will be published
        String topic = "orders";
        // Order identifier: Using the same key for all events ensures they go to the same partition.
        String orderId = "order123";

        // Simulate order events in sequence
        String[] orderEvents = {
                "Order Placed",
                "Inventory Updated",
                "Billing Processed",
                "Shipping Scheduled"
        };

        try {
            for (String event : orderEvents) {
                ProducerRecord<String, String> record = new ProducerRecord<>(topic, orderId, event);
                // Synchronously send the record so we can see the ordering (in production, async sends are common)
                RecordMetadata metadata = producer.send(record).get();
                System.out.printf("Sent event '%s' for Order ID '%s' to partition %d at offset %d%n",
                        event, orderId, metadata.partition(), metadata.offset());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            producer.close();
        }
    }
}

