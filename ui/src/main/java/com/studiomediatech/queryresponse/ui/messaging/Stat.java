package com.studiomediatech.queryresponse.ui.messaging;

import java.util.function.Consumer;

public record Stat(String key, Object value, Long timestamp, String uuid) {

    public static final String MIN_LATENCY = "min_latency";
    public static final String MAX_LATENCY = "max_latency";
    public static final String AVG_LATENCY = "avg_latency";
    public static final String COUNT_QUERIES = "count_queries";
    public static final String COUNT_CONSUMED_RESPONSES = "count_consumed_responses";
    public static final String COUNT_FALLBACKS = "count_fallbacks";
    public static final String THROUGHPUT_QUERIES = "throughput_queries";
    public static final String THROUGHPUT_RESPONSES = "throughput_responses";
    public static final String AVG_THROUGHPUT = "avg_throughput";

    @SuppressWarnings("unchecked")
    public <T> void whenKey(String key, Consumer<T> target) {
        if (this.key.equals(key)) {
            target.accept((T) value);
        }
    }
}