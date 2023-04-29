package org.example;

import com.ververica.cdc.connectors.shaded.com.fasterxml.jackson.annotation.JsonInclude;
import com.ververica.cdc.connectors.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import com.ververica.cdc.connectors.shaded.com.fasterxml.jackson.core.type.TypeReference;
import com.ververica.cdc.connectors.shaded.com.fasterxml.jackson.databind.JsonNode;
import com.ververica.cdc.connectors.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import com.ververica.cdc.connectors.shaded.com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ververica.cdc.connectors.shaded.com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.ververica.cdc.connectors.shaded.com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TimeZone;

public class DebeziumJsonUtils {
    private static final Logger log = LoggerFactory.getLogger(DebeziumJsonUtils.class);
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static final TypeReference<Map<String, ?>> MAP_TYPE_REFERENCE = new TypeReference<Map<String, ?>>() {
    };

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final TimeZone TIME_ZONE = TimeZone.getTimeZone("+8:00");

    static {
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        OBJECT_MAPPER.registerModule(module);
        OBJECT_MAPPER.setDateFormat(DATE_FORMAT);
        OBJECT_MAPPER.setTimeZone(TIME_ZONE);
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static Map<String, ?> convertMap(String content)  {
        try {
            return OBJECT_MAPPER.readValue(content, MAP_TYPE_REFERENCE);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException!", e);
            return null;
        }
    }

    public static Map<String, ?> getAfter(String content) {
        JsonNode jsonNode = null;
        try {
            jsonNode = OBJECT_MAPPER.readTree(content);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException!", e);
            return null;
        }
        JsonNode after = jsonNode.get("after");
        if (after.isNull()) {
            return null;
        }

        return OBJECT_MAPPER.convertValue(after, MAP_TYPE_REFERENCE);
    }

    public static Map<String, ?> getBefore(String content) {
        JsonNode jsonNode = null;
        try {
            jsonNode = OBJECT_MAPPER.readTree(content);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException!", e);
            return null;
        }
        JsonNode before = jsonNode.get("before");
        if (before.isNull()) {
            return null;
        }
        return OBJECT_MAPPER.convertValue(before, MAP_TYPE_REFERENCE);
    }

    public static String getTable(String content) {
        JsonNode jsonNode = null;
        try {
            jsonNode = OBJECT_MAPPER.readTree(content);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException!", e);
            return null;
        }
        JsonNode table = jsonNode.get("source").get("table");
        if (table.isNull()) {
            return null;
        }
        return table.asText();

    }

    public static String getDb(String content) {
        JsonNode jsonNode = null;
        try {
            jsonNode = OBJECT_MAPPER.readTree(content);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException!", e);
            return null;
        }
        JsonNode db = jsonNode.get("source").get("db");
        if (db.isNull()) {
            return null;
        }
        return db.asText();

    }

    public static String getOp(String content) {
        JsonNode jsonNode = null;
        try {
            jsonNode = OBJECT_MAPPER.readTree(content);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException!", e);
            return null;
        }
        JsonNode op = jsonNode.get("op");
        if (op.isNull()) {
            return null;
        }
        return op.asText();
    }

    public static String getDataPrimaryKey(String content, boolean before, String idField) {
        JsonNode jsonNode = null;
        try {
            jsonNode = OBJECT_MAPPER.readTree(content);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException!", e);
            return null;
        }
        if (before) {
            return jsonNode.get("before").get(idField).asText();
        } else {
            return jsonNode.get("after").get(idField).asText();
        }
    }
}
