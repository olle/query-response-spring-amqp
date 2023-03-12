package com.studiomediatech;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.event.EventListener;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studiomediatech.events.QueryRecordedEvent;
import com.studiomediatech.queryresponse.QueryBuilder;
import com.studiomediatech.queryresponse.ui.QueryResponseUIApp;
import com.studiomediatech.queryresponse.ui.api.RestApiAdapter;
import com.studiomediatech.queryresponse.ui.api.WebSocketApiHandler;
import com.studiomediatech.queryresponse.util.Logging;

public class QueryPublisher implements Logging, RestApiAdapter {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    // This is a Fib!
    private static final int MAX_SIZE = 2584;
    private static final int SLIDING_WINDOW = 40;
    private static final int DEFAULT_QUERY_TIMEOUT = 1500;

    static ToLongFunction<QueryPublisher.Stat> statToLong = s -> ((Number) s.value).longValue();

    private List<QueryPublisher.Stat> queries = new LinkedList<>();
    private List<QueryPublisher.Stat> responses = new LinkedList<>();
    private List<Double> successRates = new LinkedList<>();
    private List<Double> latencies = new LinkedList<>();
    private List<Double> throughputs = new LinkedList<>();
    private List<Double> tps = new LinkedList<>();

    private final QueryBuilder queryBuilder;
    private final WebSocketApiHandler handler;

    public QueryPublisher(WebSocketApiHandler handler, QueryBuilder queryBuilder) {

        this.handler = handler;
        this.queryBuilder = queryBuilder;
    }

    @Override
    public Map<String, Object> query(String q, int timeout, int limit) {

        long queryTimeout = timeout > 0 ? timeout : DEFAULT_QUERY_TIMEOUT;

        List<Object> defaults = List.of("No responses");

        final Collection<Object> responses;

        long start = System.nanoTime();

        if (limit > 0) {
            responses = queryBuilder.queryFor(q, Object.class).waitingFor(queryTimeout).takingAtMost(limit)
                    .orDefaults(defaults);
        } else {
            responses = queryBuilder.queryFor(q, Object.class).waitingFor(queryTimeout).orDefaults(defaults);
        }

        return Map.of("response", responses, "duration", Duration.ofNanos(System.nanoTime() - start));
    }

    @EventListener
    void on(QueryRecordedEvent event) {

        log().info("HANDLING {}", event);

        String query = event.getQuery();
        long timeout = event.getTimeout();

        Optional<Integer> maybe = event.getLimit();

        List<Object> orEmptyResponse = Arrays.asList("No responses");

        if (maybe.isPresent()) {
            int limit = maybe.get();

            handler.handleResponse(queryBuilder.queryFor(query, Object.class).waitingFor(timeout).takingAtMost(limit)
                    .orDefaults(orEmptyResponse), event.getPublisherId());
        } else {
            handler.handleResponse(
                    queryBuilder.queryFor(query, Object.class).waitingFor(timeout).orDefaults(orEmptyResponse),
                    event.getPublisherId());
        }
    }

    @RabbitListener(queues = "#{@" + QueryResponseUIApp.QUERY_RESPONSE_STATS_QUEUE_BEAN + "}")
    void onQueryResponseStats(Message message) {

        try {
            handle(MAPPER.readValue(message.getBody(), Stats.class).elements);
        } catch (RuntimeException | IOException ex) {
            log().error("Failed to consumed stats", ex);
        }
    }

    protected void handle(Collection<QueryPublisher.Stat> stats) {

        stats.forEach(stat -> log().debug("GOT STAT: {}", stat));

        handleCounts(stats);
        handleLatencies(stats);
        handleThroughput(stats);
        handleNodes(stats);
    }

    private void handleNodes(Collection<QueryPublisher.Stat> stats) {
        Map<String, List<QueryPublisher.Stat>> nodes = stats.stream().filter(s -> StringUtils.hasText(s.uuid))
                .collect(Collectors.groupingBy(s -> s.uuid));

        for (Entry<String, List<QueryPublisher.Stat>> node : nodes.entrySet()) {
            Stat stat = new Stat();
            stat.uuid = node.getKey();
            stat.key = "avg_throughput";
            stat.value = calculateAndAggregateThroughputAvg(queries, responses, node.getKey());
            node.getValue().add(stat);
        }

        handler.handleNodes(nodes);
    }

    private void handleThroughput(Collection<QueryPublisher.Stat> stats) {
        // Order is important!!
        double throughputQueries = calculateThroughput("throughput_queries", stats, queries);
        double throughputResponses = calculateThroughput("throughput_responses", stats, responses);
        double throughputAvg = calculateAndAggregateThroughputAvg(queries, responses, null);

        handler.handleThroughput(throughputQueries, throughputResponses, throughputAvg, throughputs);
    }

    private void handleLatencies(Collection<QueryPublisher.Stat> stats) {
        Long minLatency = stats.stream().filter(stat -> "min_latency".equals(stat.key)).mapToLong(statToLong).min()
                .orElse(-1);

        long maxLatency = stats.stream().filter(stat -> "max_latency".equals(stat.key)).mapToLong(statToLong).max()
                .orElse(-1);

        double avgLatency = stats.stream().filter(stat -> "avg_latency".equals(stat.key))
                .mapToDouble(stat -> (double) stat.value).average().orElse(0.0d);

        aggregateLatencies(avgLatency);

        handler.handleLatency(minLatency, maxLatency, avgLatency, latencies);
    }

    private void handleCounts(Collection<QueryPublisher.Stat> stats) {
        long countQueriesSum = stats.stream().filter(stat -> "count_queries".equals(stat.key)).mapToLong(statToLong)
                .sum();

        long countResponsesSum = stats.stream().filter(stat -> "count_consumed_responses".equals(stat.key))
                .mapToLong(statToLong).sum();

        long countFallbacksSum = stats.stream().filter(stat -> "count_fallbacks".equals(stat.key)).mapToLong(statToLong)
                .sum();

        double successRate = calculateAndAggregateSuccessRate(countQueriesSum, countResponsesSum);

        handler.handleCountQueriesAndResponses(countQueriesSum, countResponsesSum, countFallbacksSum, successRate,
                successRates);
    }

    private void aggregateLatencies(double avgLatency) {

        if (latencies.size() > MAX_SIZE) {
            latencies.remove(0);
        }

        latencies.add(avgLatency);
    }

    private double calculateAndAggregateSuccessRate(long countQueriesSum, long countResponsesSum) {

        double n = 1.0 * countResponsesSum;
        double d = 1.0 * Math.max(1.0, countQueriesSum);

        double rate = Math.round((n / d) * 100.0 * 10.0) / 10.0;

        if (successRates.size() > MAX_SIZE) {
            successRates.remove(0);
        }

        successRates.add(rate);

        return rate;
    }

    private double calculateAndAggregateThroughputAvg(List<QueryPublisher.Stat> queries,
            List<QueryPublisher.Stat> responses, String node) {

        List<QueryPublisher.Stat> all = new ArrayList<>();

        if (node != null) {
            all.addAll(queries.stream().filter(s -> node.equals(s.uuid)).collect(Collectors.toList()));
            all.addAll(responses.stream().filter(s -> node.equals(s.uuid)).collect(Collectors.toList()));
        } else {
            all.addAll(queries);
            all.addAll(responses);
        }

        all.sort(Comparator.comparing(s -> s.timestamp));

        if (all.size() < 2) {
            return 0.0;
        }

        long newest = all.get(all.size() - 1).timestamp;
        long oldest = all.get(0).timestamp;
        long duration = (newest - oldest) / 1000;

        if (duration < 1) {
            return 0.0;
        }

        double sum = 1.0 * all.stream().mapToLong(statToLong).sum();
        double tp = Math.round((sum / duration) * 1000000.0) / 1000000.0;

        if (tps.size() > SLIDING_WINDOW) {
            tps.remove(0);
        }

        tps.add(tp);

        if (throughputs.size() > MAX_SIZE) {
            throughputs.remove(0);
        }

        double avg = tps.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        throughputs.add(avg);

        return avg;
    }

    private double calculateThroughput(String key, Collection<QueryPublisher.Stat> source,
            List<QueryPublisher.Stat> dest) {

        List<QueryPublisher.Stat> ts = source.stream().filter(stat -> key.equals(stat.key))
                .sorted(Comparator.comparing(s -> s.timestamp)).collect(Collectors.toList());

        for (QueryPublisher.Stat stat : ts) {
            if (dest.size() > MAX_SIZE) {
                dest.remove(0);
            }

            dest.add(stat);
        }

        if (dest.size() < 2) {
            return 0.0;
        }

        long newest = dest.get(dest.size() - 1).timestamp;
        long oldest = dest.get(0).timestamp;
        long duration = (newest - oldest) / 1000;

        if (duration < 1) {
            return 0.0;
        }

        double sum = 1.0 * dest.stream().mapToLong(statToLong).sum();

        return Math.round((sum / duration) * 1000000.0) / 1000000.0;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Stats {

        @JsonProperty
        public Collection<Stat> elements;

        @Override
        public String toString() {

            return Optional.ofNullable(elements).orElse(Collections.emptyList()).stream().map(Object::toString)
                    .collect(Collectors.joining(", "));
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Stat {

        @JsonProperty
        public String key;
        @JsonProperty
        public Object value;
        @JsonProperty
        public Long timestamp;
        @JsonProperty
        public String uuid;

        @Override
        public String toString() {

            return key + "=" + value + (timestamp != null ? " " + timestamp : "")
                    + (uuid != null ? " uuid=" + uuid : "");
        }
    }

}
