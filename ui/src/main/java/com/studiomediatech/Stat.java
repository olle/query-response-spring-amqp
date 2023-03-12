package com.studiomediatech;

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

    // @Override
    // public String toString() {
    //
    // return key + "=" + value + (timestamp != null ? " " + timestamp : "")
    // + (uuid != null ? " uuid=" + uuid : "");
    // }
}