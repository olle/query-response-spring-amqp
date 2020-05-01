package com.studiomediatech.queryresponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.studiomediatech.queryresponse.util.Logging;

import org.springframework.context.ApplicationContext;

import org.springframework.core.env.Environment;

import org.springframework.util.StringUtils;

import java.lang.management.ManagementFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;


class Statistics implements Logging {

    // Yes, it's a FIB!!
    private static final int MAX_COLLECTED_LATENCIES = 987;

    private static final String STAT_UPTIME = "uptime";
    private static final String STAT_PID = "pid";
    private static final String STAT_HOSTNAME = "hostname";
    private static final String STAT_NAME = "name";
    private static final String STAT_COUNT_QUERIES = "count_queries";
    private static final String STAT_COUNT_CONSUMED_RESPONSES = "count_consumed_responses";
    private static final String STAT_COUNT_PUBLISHED_RESPONSES = "count_published_responses";
    private static final String STAT_COUNT_FALLBACKS = "count_fallbacks";
    private static final String STAT_LATENCY_MAX = "max_latency";
    private static final String STAT_LATENCY_MIN = "min_latency";
    private static final String STAT_LATENCY_AVG = "avg_latency";
    private static final String STAT_TP_QUERIES = "throughput_queries";
    private static final String STAT_TP_RESPONSES = "throughput_responses";

    private final Environment env;
    private final ApplicationContext ctx;

    private AtomicLong publishedQueriesCount = new AtomicLong(0);
    private AtomicLong consumedResponsesCount = new AtomicLong(0);
    private AtomicLong publishedResponsesCount = new AtomicLong(0);
    private AtomicLong fallbacksCount = new AtomicLong(0);
    private List<Long> latencies = new LinkedList<>(Arrays.asList(0L));
    private AtomicLong lastQueriesCount = new AtomicLong(0);
    private AtomicLong lastResponsesCount = new AtomicLong(0);

    public Statistics(Environment env, ApplicationContext ctx) {

        this.env = env;
        this.ctx = ctx;

        Executors.newScheduledThreadPool(1).schedule(this::respond, 1L, TimeUnit.SECONDS);
    }

    void respond() {

        ResponseBuilder.respondTo("query-response/stats", Stat.class)
            .withAll()
            .suppliedBy(this::getStats);
    }


    protected Collection<Stat> getStats() {

        return List.of( // NOSONAR
                Stat.of(STAT_COUNT_QUERIES, this.publishedQueriesCount.get()), // NOSONAR
                Stat.of(STAT_COUNT_CONSUMED_RESPONSES, this.consumedResponsesCount.get()), // NOSONAR
                Stat.of(STAT_COUNT_PUBLISHED_RESPONSES, this.publishedResponsesCount.get()), // NOSONAR
                Stat.of(STAT_COUNT_FALLBACKS, this.fallbacksCount.get()), // NOSONAR
                Stat.of(STAT_NAME, getApplicationNameOrDefault("application")), // NOSONAR
                Stat.of(STAT_HOSTNAME, getHostnameOrDefault("unknown")), // NOSONAR
                Stat.of(STAT_PID, getPidOrDefault("-")), // NOSONAR
                Stat.of(STAT_UPTIME, getUptimeOrDefault("-")), // NOSONAR
                Stat.of(STAT_LATENCY_MAX, getMaxLatency()), // NOSONAR
                Stat.of(STAT_LATENCY_MIN, getMinLatency()), // NOSONAR
                Stat.of(STAT_LATENCY_AVG, getAvgLatency()), // NOSONAR
                getThroughputQueriesStat(), // NOSONAR
                getThroughputResponsesStat() // NOSONAR
                );
    }


    protected Stat getThroughputQueriesStat() {

        long current = this.publishedQueriesCount.get();

        return Stat.at(STAT_TP_QUERIES, current - this.lastQueriesCount.getAndSet(current));
    }


    protected Stat getThroughputResponsesStat() {

        long current = this.consumedResponsesCount.get();

        return Stat.at(STAT_TP_RESPONSES, current - this.lastResponsesCount.getAndSet(current));
    }


    protected double getAvgLatency() {

        return latencies.stream().collect(Collectors.summarizingLong(Long::valueOf)).getAverage();
    }


    protected long getMinLatency() {

        return latencies.stream().collect(Collectors.summarizingLong(Long::valueOf)).getMin();
    }


    protected long getMaxLatency() {

        return latencies.stream().collect(Collectors.summarizingLong(Long::valueOf)).getMax();
    }


    protected String getUptimeOrDefault(String defaults) {

        return Stream.of(Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime()).toString())
            .filter(StringUtils::hasText)
            .findFirst()
            .orElse(defaults);
    }


    protected String getPidOrDefault(String defaults) {

        try {
            return "" + ProcessHandle.current().pid();
        } catch (UnsupportedOperationException e) {
            // Dang!
        }

        return defaults;
    }


    protected String getHostnameOrDefault(String defaults) {

        String hostName = null;

        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            // Meh!
        }

        return Stream.of(System.getProperty("HOSTNAME"), hostName, System.getProperty("COMPUTERNAME"))
            .filter(StringUtils::hasText)
            .findFirst()
            .orElse(defaults);
    }


    protected String getApplicationNameOrDefault(String defaults) {

        return Stream.of(env.getProperty("cola.id"), env.getProperty("spring.application.name"),
                    ctx.getApplicationName(), ctx.getId(), ctx.getDisplayName())
            .filter(StringUtils::hasText)
            .findFirst()
            .orElse(defaults);
    }


    public void incrementPublishedQueriesCounter() {

        this.publishedQueriesCount.incrementAndGet();
    }


    public void incrementConsumedResponsesCounter() {

        this.consumedResponsesCount.incrementAndGet();
    }


    public void incrementPublishedResponsesCounter() {

        this.publishedResponsesCount.incrementAndGet();
    }


    public void incrementFallbacksCounter() {

        this.fallbacksCount.incrementAndGet();
    }


    public void measureLatency(Long publishedAtTimestamp, Long currentTimestamp) {

        if (publishedAtTimestamp == null) {
            return;
        }

        long latency = currentTimestamp - publishedAtTimestamp;

        if (latency < 1) {
            return;
        }

        if (latencies.size() > MAX_COLLECTED_LATENCIES) {
            latencies.remove(0);
        }

        latencies.add(latency);
    }

    @JsonInclude(Include.NON_NULL)
    public static final class Stat {

        @JsonProperty
        public String key;
        @JsonProperty
        public Object value;
        @JsonProperty
        public Long timestamp;

        private Stat(String key, Object value) {

            this.key = key;
            this.value = value;
        }


        public Stat(String key, Object value, long timestamp) {

            this(key, value);
            this.timestamp = timestamp;
        }

        public static Stat of(String key, Object value) {

            return new Stat(key, value);
        }


        public static Stat at(String key, Object value) {

            return new Stat(key, value, Instant.now(Clock.systemUTC()).toEpochMilli());
        }
    }
}
