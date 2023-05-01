package org.example;

import com.ververica.cdc.connectors.shaded.com.fasterxml.jackson.annotation.JsonInclude;
import com.ververica.cdc.connectors.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import com.ververica.cdc.connectors.shaded.com.fasterxml.jackson.core.type.TypeReference;
import com.ververica.cdc.connectors.shaded.com.fasterxml.jackson.databind.JsonMappingException;
import com.ververica.cdc.connectors.shaded.com.fasterxml.jackson.databind.JsonNode;
import com.ververica.cdc.connectors.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import com.ververica.cdc.connectors.shaded.com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.ververica.cdc.connectors.shaded.com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.support.DateDeserializer;
import org.example.support.DateSerializer;
import org.example.support.LocalDateTimeDeserializer;
import org.example.support.LocalDateTimeSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Date;

public class ObjectMapperUtils {
    private static final Logger log = LoggerFactory.getLogger(DebeziumJsonUtils.class);
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        module.addSerializer(Date.class, new DateSerializer());
        module.addDeserializer(Date.class, new DateDeserializer());
        OBJECT_MAPPER.registerModule(module);
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        OBJECT_MAPPER.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    }

    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    public static <T> T convertValue(Object fromValue, Class<T> toValueType) throws IllegalArgumentException {
        return OBJECT_MAPPER.convertValue(fromValue, toValueType);
    }

    public static <T> T convertValue(Object fromValue, TypeReference<T> toValueTypeRef) throws IllegalArgumentException {
        return OBJECT_MAPPER.convertValue(fromValue, toValueTypeRef);
    }

    public static <T> T readValue(String content, Class<T> valueType) throws JsonProcessingException, JsonMappingException {
        return OBJECT_MAPPER.readValue(content, valueType);
    }

    public static <T> T readValue(String content, TypeReference<T> valueTypeRef) throws JsonProcessingException, JsonMappingException {
        return OBJECT_MAPPER.readValue(content, valueTypeRef);
    }

    public static JsonNode readTree(String content) throws JsonProcessingException {
        return OBJECT_MAPPER.readTree(content);
    }

    public static String writeValueAsString(Object value) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(value);
    }

}
