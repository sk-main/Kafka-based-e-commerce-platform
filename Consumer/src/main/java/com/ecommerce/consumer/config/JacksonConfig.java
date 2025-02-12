package com.ecommerce.consumer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Register module to handle Java 8 date/time types (Instant, etc.)
        mapper.registerModule(new JavaTimeModule());
        // Optionally disable writing dates as timestamps for better readability
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Register the mix-in for all Avro-generated classes
        mapper.addMixIn(SpecificRecordBase.class, AvroSchemaMixIn.class);

        return mapper;
    }
}

