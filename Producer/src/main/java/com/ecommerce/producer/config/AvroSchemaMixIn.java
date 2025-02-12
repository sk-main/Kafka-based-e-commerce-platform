package com.ecommerce.producer.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import org.apache.avro.Schema;

@JsonIgnoreType
abstract class AvroSchemaMixIn {
    @JsonIgnore abstract Schema getSchema();
    @JsonIgnore abstract void setSchema(Schema schema);
}

