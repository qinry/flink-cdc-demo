package org.example.support;

import com.ververica.cdc.connectors.shaded.com.fasterxml.jackson.core.JsonParser;
import com.ververica.cdc.connectors.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import com.ververica.cdc.connectors.shaded.com.fasterxml.jackson.core.JsonToken;
import com.ververica.cdc.connectors.shaded.com.fasterxml.jackson.databind.DeserializationContext;
import com.ververica.cdc.connectors.shaded.com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        if (jsonParser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            long valueAsLong = jsonParser.getValueAsLong();
            Instant instant = Instant.ofEpochMilli(valueAsLong);
            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
            return localDateTime;
        }
        String valueAsString = jsonParser.getValueAsString();
        LocalDateTime localDateTime = LocalDateTime.parse(valueAsString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return localDateTime;
    }
}
