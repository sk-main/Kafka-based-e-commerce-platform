package com.ecommerce.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class OrderConsumer {

    public static void main(String[] args) {
        // Kafka Consumer configuration
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");  // Adjust as necessary
        // Each downstream service should have a unique group id.

        props.put("group.id", "inventory-group");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        // Auto commit offsets for simplicity (in production you might commit manually)
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");

        // Create the KafkaConsumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        // Subscribe to the topic containing order events
        consumer.subscribe(Arrays.asList("orders"));

        System.out.println("Consumer is now waiting for order events...");

        try {
            // Poll for new data continuously
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("Service [inventory]: Received event '%s' for Order ID '%s' from partition %d at offset %d%n",
                            record.value(), record.key(), record.partition(), record.offset());
                    // Here, the inventory service would process the event accordingly.
                }
            }
        } finally {
            consumer.close();
        }
    }
}

