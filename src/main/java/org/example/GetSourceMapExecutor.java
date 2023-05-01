package org.example;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class GetSourceMapExecutor {
    private static final Map<String, Function<String, String>> GET_ID_EXECUTORS = new ConcurrentHashMap<>();
    private static final Map<String, Function<String, ?>> GET_SOURCE_MAP_EXECUTORS = new ConcurrentHashMap<>();

    public static void putGetIdFunction(String table, Function<String, String> function) {
        GET_ID_EXECUTORS.put(table, function);
    }

    public static String getIdValue(String table, String element) {
        return GET_ID_EXECUTORS.get(table).apply(element);
    }

    public static void putGetSourceMapFunction(String table, Function<String, ?> function) {
        GET_SOURCE_MAP_EXECUTORS.put(table, function);
    }

    public static Object getSource(String table, String element) {
        return GET_SOURCE_MAP_EXECUTORS.get(table).apply(element);
    }
}
