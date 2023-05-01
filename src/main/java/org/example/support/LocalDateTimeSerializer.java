package org.example.support;

import com.ververica.cdc.connectors.shaded.com.fasterxml.jackson.core.JsonGenerator;
import com.ververica.cdc.connectors.shaded.com.fasterxml.jackson.databind.JsonSerializer;
import com.ververica.cdc.connectors.shaded.com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
    @Override
    public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String localDateTimeStr = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        jsonGenerator.writeObject(localDateTimeStr);
    }
}
