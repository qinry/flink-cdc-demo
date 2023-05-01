package org.example;

import com.ververica.cdc.connectors.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import com.ververica.cdc.connectors.shaded.com.fasterxml.jackson.core.type.TypeReference;
import com.ververica.cdc.connectors.shaded.com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class DebeziumJsonUtils {
    private static final Logger log = LoggerFactory.getLogger(DebeziumJsonUtils.class);

    public static final TypeReference<Map<String, ?>> MAP_TYPE_REFERENCE = new TypeReference<Map<String, ?>>() {
    };

    public static Map<String, ?> convertMap(Object content) {
        try {
            return ObjectMapperUtils.convertValue(content, MAP_TYPE_REFERENCE);
        } catch (IllegalArgumentException e) {
            log.error("IllegalArgumentException!", e);
            return null;
        }
    }

    public static <T> T convertEntity(String content, Class<T> clazz) {
        try {
            return ObjectMapperUtils.readValue(content, clazz);
        } catch (Exception e) {
            log.error("Exception!", e);
            return null;
        }
    }

    public static Map<String, ?> getAfter(String content) {
        JsonNode jsonNode = null;
        try {
            jsonNode = ObjectMapperUtils.readTree(content);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException!", e);
            return null;
        }
        JsonNode after = jsonNode.get("after");
        if (after.isNull()) {
            return null;
        }

        return ObjectMapperUtils.convertValue(after, MAP_TYPE_REFERENCE);
    }

    public static <T> T getAfter(String content, Class<T> clazz) {
        JsonNode jsonNode = null;
        try {
            jsonNode = ObjectMapperUtils.readTree(content);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException!", e);
            return null;
        }
        JsonNode after = jsonNode.get("after");
        if (after.isNull()) {
            return null;
        }

        return ObjectMapperUtils.convertValue(after, clazz);
    }

    public static String getAfterString(String content) {
        JsonNode jsonNode = null;
        try {
            jsonNode = ObjectMapperUtils.readTree(content);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException!", e);
            return null;
        }
        JsonNode after = jsonNode.get("after");
        if (after.isNull()) {
            return null;
        }

        return after.toString();
    }

    public static Map<String, ?> getBefore(String content) {
        JsonNode jsonNode = null;
        try {
            jsonNode = ObjectMapperUtils.readTree(content);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException!", e);
            return null;
        }
        JsonNode before = jsonNode.get("before");
        if (before.isNull()) {
            return null;
        }
        return ObjectMapperUtils.convertValue(before, MAP_TYPE_REFERENCE);
    }

    public static <T> T getBefore(String content, Class<T> clazz) {
        JsonNode jsonNode = null;
        try {
            jsonNode = ObjectMapperUtils.readTree(content);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException!", e);
            return null;
        }
        JsonNode before = jsonNode.get("before");
        if (before.isNull()) {
            return null;
        }
        return ObjectMapperUtils.convertValue(before, clazz);
    }

    public static String getBeforeString(String content) {
        JsonNode jsonNode = null;
        try {
            jsonNode = ObjectMapperUtils.readTree(content);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException!", e);
            return null;
        }
        JsonNode before = jsonNode.get("before");
        if (before.isNull()) {
            return null;
        }
        return before.toString();
    }

    public static String getTable(String content) {
        JsonNode jsonNode = null;
        try {
            jsonNode = ObjectMapperUtils.readTree(content);
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
            jsonNode = ObjectMapperUtils.readTree(content);
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
            jsonNode = ObjectMapperUtils.readTree(content);
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

    public static String getDataPrimaryKey(String content, String idField) {
        JsonNode jsonNode = null;
        try {
            jsonNode = ObjectMapperUtils.readTree(content);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException!", e);
            return null;
        }
        return jsonNode.get(idField).asText();
    }
}
