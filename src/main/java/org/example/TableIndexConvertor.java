package org.example;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TableIndexConvertor {
    private static final Map<String, String> MAP = new ConcurrentHashMap<>();

    public static void put(String tableName, String indexName) {
        MAP.put(tableName, indexName);
    }

    public static String fromTable(String table) {
        return MAP.getOrDefault(table, "");
    }
}
