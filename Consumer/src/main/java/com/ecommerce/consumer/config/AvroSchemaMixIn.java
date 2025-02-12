package com.ecommerce.consumer.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.avro.Schema;

public abstract class AvroSchemaMixIn {
    @JsonIgnore
    public abstract Schema getSchema();
}
