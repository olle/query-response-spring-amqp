package com.studiomediatech.queryresponse.ui.app.telemetry;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

import com.studiomediatech.queryresponse.ui.app.adapter.QueryPublisherAdapter;
import com.studiomediatech.queryresponse.ui.app.adapter.RestApiAdapter;
import com.studiomediatech.queryresponse.ui.app.adapter.TelemetryHandlerAdapter;
import com.studiomediatech.queryresponse.ui.app.adapter.WebSocketApiAdapter;
import com.studiomediatech.queryresponse.ui.messaging.Stat;
import com.studiomediatech.queryresponse.ui.messaging.Stats;
import com.studiomediatech.queryresponse.util.Loggable;

public class TelemetryService implements Loggable, TelemetryHandlerAdapter, RestApiAdapter {

    private static final int MAX_HISTORY_SIZE = 2584;
    private static final int SLIDING_WINDOW = 40;
    private static final Duration NODE_TTL = Duration.ofMinutes(2);

    private static final ToLongFunction<Stat> STAT_TO_LONG = stat -> ((Number) stat.value()).longValue();

    private final WebSocketApiAdapter webSocketApiAdapter;
    private final NodeRepository nodeRepository;
    private final QueryPublisherAdapter queryPublisherAdapter;

    private final List<Stat> queryThroughputs = new LinkedList<>();
    private final List<Stat> responseThroughputs = new LinkedList<>();
    private final List<Double> successRates = new LinkedList<>();
    private final List<Double> latencies = new LinkedList<>();
    private final List<Double> avgThroughputs = new LinkedList<>();
    private final List<Double> throughputSamples = new LinkedList<>();

    private final Map<UUID, Instant> lastSeenByNode = new ConcurrentHashMap<>();
    private final Map<UUID, List<Stat>> statsByNode = new ConcurrentHashMap<>();

    private Map<String, Object> latestMetrics;

    public TelemetryService(WebSocketApiAdapter webSocketApiAdapter, NodeRepository nodeRepository,
            QueryPublisherAdapter queryPublisherAdapter) {
        this.webSocketApiAdapter = webSocketApiAdapter;
        this.nodeRepository = nodeRepository;
        this.queryPublisherAdapter = queryPublisherAdapter;
    }

    @Override
    public Map<String, Object> query(String q, int timeout, int limit) {
        return queryPublisherAdapter.query(q, timeout, limit);
    }

    @Override
    public Map<String, Object> nodes() {
        return Map.of("elements", nodeRepository.findAll());
    }

    @Override
    public synchronized void handleConsumed(Stats stats) {

        logger().debug("Consumed stats with {} elements", stats.elements().size());

        updateNodes(parseToNodesCollection(stats));
        aggregateStats(stats.elements());
    }

    private void updateNodes(Collection<Node> nodes) {
        for (Node node : nodes) {
            Optional<Node> maybe = nodeRepository.findOneByUUID(node.getUUID());
            if (maybe.isPresent()) {
                Node updated = maybe.get().update(node);
                nodeRepository.save(updated);
            } else {
                nodeRepository.save(node);
            }
        }
    }

    private void aggregateStats(Collection<Stat> stats) {

        Map<UUID, List<Stat>> byNode = groupByNode(stats);
        Instant now = Instant.now();

        for (var entry : byNode.entrySet()) {
            lastSeenByNode.put(entry.getKey(), now);
            statsByNode.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }

        // Order is important: the per-node throughput is derived from the accumulated throughput history.
        double throughputQueries = appendThroughput(Stat.THROUGHPUT_QUERIES, stats, queryThroughputs);
        double throughputResponses = appendThroughput(Stat.THROUGHPUT_RESPONSES, stats, responseThroughputs);
        double avgThroughput = calculateAndAggregateThroughputAvg();

        for (var entry : byNode.entrySet()) {
            List<Stat> nodeStats = statsByNode.get(entry.getKey());
            if (nodeStats != null) {
                nodeStats.add(new Stat(Stat.AVG_THROUGHPUT, calculateThroughputAvg(entry.getKey()), now.toEpochMilli(),
                        entry.getKey().toString()));
            }
        }

        evictStaleNodes();

        latestMetrics = calculateMetrics(throughputQueries, throughputResponses, avgThroughput);
    }

    private Map<String, Object> calculateMetrics(double throughputQueries, double throughputResponses,
            double avgThroughput) {

        Collection<Stat> latest = statsByNode.values().stream().flatMap(Collection::stream).toList();

        long countQueries = sum(latest, Stat.COUNT_QUERIES);
        long countResponses = sum(latest, Stat.COUNT_CONSUMED_RESPONSES);
        long countFallbacks = sum(latest, Stat.COUNT_FALLBACKS);
        double successRate = calculateAndAggregateSuccessRate(countQueries, countResponses);

        long minLatency = latest.stream().filter(isKey(Stat.MIN_LATENCY)).mapToLong(STAT_TO_LONG).min().orElse(0);
        long maxLatency = latest.stream().filter(isKey(Stat.MAX_LATENCY)).mapToLong(STAT_TO_LONG).max().orElse(0);
        double avgLatency = latest.stream().filter(isKey(Stat.AVG_LATENCY))
                .mapToDouble(stat -> ((Number) stat.value()).doubleValue()).average().orElse(0.0d);
        aggregateLatency(avgLatency);

        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("count_queries", countQueries);
        metrics.put("count_responses", countResponses);
        metrics.put("count_fallbacks", countFallbacks);
        metrics.put("success_rate", successRate);
        metrics.put("success_rates", List.copyOf(successRates));
        metrics.put("min_latency", minLatency);
        metrics.put("max_latency", maxLatency);
        metrics.put("avg_latency", avgLatency);
        metrics.put("avg_latencies", List.copyOf(latencies));
        metrics.put("throughput_queries", throughputQueries);
        metrics.put("throughput_responses", throughputResponses);
        metrics.put("avg_throughput", avgThroughput);
        metrics.put("avg_throughputs", List.copyOf(avgThroughputs));

        return metrics;
    }

    protected Collection<Node> parseToNodesCollection(Stats stats) {

        Map<UUID, Node> nodes = new HashMap<>();

        for (Stat stat : stats.elements()) {

            if (!StringUtils.hasText(stat.uuid())) {
                continue;
            }

            UUID uuid = UUID.fromString(stat.uuid());
            Node node = nodes.computeIfAbsent(uuid, Node::from);

            stat.whenKey("name", node::setName);
            stat.whenKey("pid", node::setPid);
            stat.whenKey("host", node::setHost);
            stat.whenKey("uptime", node::setUptime);
        }

        return nodes.values();
    }

    public synchronized void publish() {

        webSocketApiAdapter.publishNodes(currentNodes());

        if (latestMetrics != null) {
            webSocketApiAdapter.publishMetrics(latestMetrics);
        }
    }

    private Map<String, List<Stat>> currentNodes() {

        evictStaleNodes();

        Instant cutoff = Instant.now().minus(NODE_TTL);

        Map<String, List<Stat>> nodes = new LinkedHashMap<>();

        for (var entry : statsByNode.entrySet()) {
            Instant lastSeen = lastSeenByNode.get(entry.getKey());
            if (lastSeen == null || lastSeen.isBefore(cutoff)) {
                continue;
            }
            nodes.put(entry.getKey().toString(), List.copyOf(entry.getValue()));
        }

        return nodes;
    }

    private static Map<UUID, List<Stat>> groupByNode(Collection<Stat> stats) {
        return stats.stream().filter(stat -> StringUtils.hasText(stat.uuid()))
                .collect(Collectors.groupingBy(stat -> UUID.fromString(stat.uuid())));
    }

    private double appendThroughput(String key, Collection<Stat> stats, List<Stat> target) {

        List<Stat> ordered = stats.stream().filter(isKey(key)).sorted(Comparator.comparing(Stat::timestamp)).toList();

        for (Stat stat : ordered) {
            if (target.size() > MAX_HISTORY_SIZE) {
                target.remove(0);
            }
            target.add(stat);
        }

        return calculateRate(target);
    }

    private double calculateThroughputAvg(UUID node) {

        String uuid = node.toString();

        List<Stat> all = new ArrayList<>();
        all.addAll(queryThroughputs.stream().filter(stat -> uuid.equals(stat.uuid())).toList());
        all.addAll(responseThroughputs.stream().filter(stat -> uuid.equals(stat.uuid())).toList());
        all.sort(Comparator.comparing(Stat::timestamp));

        return calculateRate(all);
    }

    private double calculateAndAggregateThroughputAvg() {

        List<Stat> all = new ArrayList<>();
        all.addAll(queryThroughputs);
        all.addAll(responseThroughputs);
        all.sort(Comparator.comparing(Stat::timestamp));

        double rate = calculateRate(all);

        if (throughputSamples.size() > SLIDING_WINDOW) {
            throughputSamples.remove(0);
        }
        throughputSamples.add(rate);

        if (avgThroughputs.size() > MAX_HISTORY_SIZE) {
            avgThroughputs.remove(0);
        }
        double avg = throughputSamples.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        avgThroughputs.add(avg);

        return avg;
    }

    private static double calculateRate(List<Stat> stats) {

        if (stats.size() < 2) {
            return 0.0;
        }

        long newest = stats.get(stats.size() - 1).timestamp();
        long oldest = stats.get(0).timestamp();
        long duration = (newest - oldest) / 1000;

        if (duration < 1) {
            return 0.0;
        }

        double sum = stats.stream().mapToLong(STAT_TO_LONG).sum();

        return Math.round((sum / duration) * 1000000.0) / 1000000.0;
    }

    private double calculateAndAggregateSuccessRate(long countQueries, long countResponses) {

        double n = 1.0 * countResponses;
        double d = 1.0 * Math.max(1.0, countQueries);

        double rate = Math.round((n / d) * 100.0 * 10.0) / 10.0;

        appendBounded(successRates, rate);

        return rate;
    }

    private void aggregateLatency(double avgLatency) {
        appendBounded(latencies, avgLatency);
    }

    private static void appendBounded(List<Double> values, double value) {

        if (values.size() > MAX_HISTORY_SIZE) {
            values.remove(0);
        }

        values.add(value);
    }

    private static long sum(Collection<Stat> stats, String key) {
        return stats.stream().filter(isKey(key)).mapToLong(STAT_TO_LONG).sum();
    }

    private static Predicate<Stat> isKey(String key) {
        return stat -> key.equals(stat.key());
    }

    private void evictStaleNodes() {

        Instant cutoff = Instant.now().minus(NODE_TTL);

        lastSeenByNode.entrySet().removeIf(entry -> {
            if (entry.getValue().isBefore(cutoff)) {
                statsByNode.remove(entry.getKey());
                nodeRepository.remove(entry.getKey());
                return true;
            }
            return false;
        });
    }

}
