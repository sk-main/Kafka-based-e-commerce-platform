# Kafka-based-e-commerce-platform

## 1. Developer Details

- **Full Name:** Shalev Kedar  
- **ID Number:** 314714080


---

## 2. Kafka Topics and Their Purpose

- **`orders` Topic:**  
  - **Purpose:** This topic is used by both the producer and consumer applications.  
  - **Producer Role:** All order events—whether they are created or updated—are published to the `orders` topic. This includes new orders and updates to existing orders (for example, a change in order status).  
  - **Consumer Role:** The consumer listens to the `orders` topic, processes incoming messages (checks for duplicates, calculates shipping costs, etc.), and stores the processed orders in an in‑memory repository for further retrieval via REST API endpoints.

---

## 3. Message Key

- **Key Used:** `orderId`  
- **Reason for Using `orderId` as the Key:**  
  - **Ordering Guarantee:** Kafka guarantees that all messages within a single partition are delivered in the order they were produced. By using the `orderId` as the key, all events related to a particular order are sent to the same partition. This ensures that the consumer processes the events in the correct sequence.
  - **Efficient Lookups and Processing:** Using `orderId` allows consumers and downstream systems to quickly identify and process events for a given order.

---

## 4. Error Handling Approaches

### How Errors Are Handled:

1. **Synchronous Kafka Send with `.get()` and Try/Catch Blocks:**
   - In the producer API endpoints (for both creating and updating orders), messages are sent to Kafka synchronously using `kafkaTemplate.send(...).get()`.  
   - **Why:** This approach ensures that any issues (e.g., connection problems, unavailable brokers) are caught immediately during the API call, allowing the system to return an error response directly to the client.

2. **Specific Exception Handling:**
   - **InterruptedException:**  
     - **Scenario:** Thrown if the thread is interrupted while waiting for Kafka to acknowledge the message send.
     - **Response:** The thread’s interrupted status is restored, and a `500 Internal Server Error` is returned with a message indicating that the operation was interrupted.
   - **ExecutionException:**  
     - **Scenario:** Thrown if the Kafka send operation fails due to issues like connection timeouts or broker unavailability.
     - **Response:** A `503 Service Unavailable` status is returned along with the underlying error message to indicate that the issue is temporary or related to Kafka connectivity.
   - **KafkaException:**  
     - **Scenario:** Catches any other Kafka-specific issues not covered by the above exceptions.
     - **Response:** Also returns a `503 Service Unavailable` response with details of the Kafka error.

3. **API-Level Error Responses:**
   - The REST endpoints are designed to return clear HTTP status codes and messages. For example:
     - **201 Created** for successfully created orders.
     - **200 OK** for successfully updated orders.
     - **404 Not Found** if an order to update cannot be found.
     - **500 Internal Server Error** or **503 Service Unavailable** for errors encountered during Kafka operations.
  
4. **Why These Approaches Were Chosen:**
   - **Immediate Feedback:** By handling errors synchronously (using `.get()`), clients receive immediate feedback if something goes wrong, which is crucial for troubleshooting and maintaining system reliability.
   - **Granular Exception Handling:** Catching different exception types (e.g., `InterruptedException`, `ExecutionException`, `KafkaException`) allows the API to return more precise error messages and HTTP status codes, helping clients understand the nature of the failure.
   - **Standard Practices:** These error-handling patterns are common in Kafka-based applications and ensure that transient errors (such as temporary broker outages) are properly communicated, potentially triggering retries or fallback mechanisms on the client side.

---

## Final Notes

- **Schema Registry Integration:**  
  The project uses Confluent’s Schema Registry and Avro format for both the producer and consumer. The Schema Registry URL is configured (e.g., `http://localhost:8085`) based on your deployment (using Docker Compose or a standalone installation).

- **Project Structure:**  
  The producer and consumer are separate standalone Maven projects, each with its own `pom.xml`. Both projects include the necessary dependencies (such as `kafka-avro-serializer` for the producer and `kafka-avro-deserializer` for the consumer) and are configured to use the same Avro schema (located in `src/main/avro`).

