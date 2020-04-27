package com.studiomediatech.queryresponse;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.studiomediatech.queryresponse.util.Logging;

import org.springframework.boot.context.event.ApplicationReadyEvent;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;

import org.springframework.core.env.Environment;

import org.springframework.scheduling.annotation.Async;

import org.springframework.util.StringUtils;

import java.lang.management.ManagementFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.time.Duration;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;


class Statistics implements Logging {

    private static final String STAT_UPTIME = "uptime";
    private static final String STAT_PID = "pid";
    private static final String STAT_HOSTNAME = "hostname";
    private static final String STAT_NAME = "name";
    private static final String STAT_COUNT_QUERIES = "count_queries";
    private static final String STAT_COUNT_RESPONSES = "count_responses";
    private static final String STAT_COUNT_FALLBACKS = "count_fallbacks";

    private final Environment env;
    private final ApplicationContext ctx;

    private AtomicLong queriesCount = new AtomicLong(0);
    private AtomicLong responsesCount = new AtomicLong(0);
    private AtomicLong fallbacksCount = new AtomicLong(0);

    private List<Long> latencies = new LinkedList<>();

    public Statistics(Environment env, ApplicationContext ctx) {

        this.env = env;
        this.ctx = ctx;
    }

    @Async
    @EventListener
    void on(ApplicationReadyEvent event) {

        ResponseBuilder.respondTo("query-response/stats", Stat.class)
            .withAll()
            .suppliedBy(this::getStats);
    }


    protected Collection<Stat> getStats() {

        return List.of( // NOSONAR
                Stat.of(STAT_COUNT_QUERIES, this.queriesCount.get()), // NOSONAR
                Stat.of(STAT_COUNT_RESPONSES, this.responsesCount.get()), // NOSONAR
                Stat.of(STAT_COUNT_FALLBACKS, this.fallbacksCount.get()), // NOSONAR
                Stat.of(STAT_NAME, getApplicationNameOrDefault("application")), // NOSONAR
                Stat.of(STAT_HOSTNAME, getHostnameOrDefault("unknown")), // NOSONAR
                Stat.of(STAT_PID, getPidOrDefault("-")), // NOSONAR
                Stat.of(STAT_UPTIME, getUptimeOrDefault("-")) // NOSONAR
                );
    }


    private String getUptimeOrDefault(String defaults) {

        return Stream.of(Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime()).toString())
            .filter(StringUtils::hasText)
            .findFirst()
            .orElse(defaults);
    }


    private String getPidOrDefault(String defaults) {

        try {
            return "" + ProcessHandle.current().pid();
        } catch (UnsupportedOperationException e) {
            // Dang!
        }

        return defaults;
    }


    private String getHostnameOrDefault(String defaults) {

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


    private String getApplicationNameOrDefault(String defaults) {

        return Stream.of(env.getProperty("cola.id"), env.getProperty("spring.application.name"),
                    ctx.getApplicationName(), ctx.getId(), ctx.getDisplayName())
            .filter(StringUtils::hasText)
            .findFirst()
            .orElse(defaults);
    }


    public void incrementQueriesCounter() {

        this.queriesCount.incrementAndGet();
    }


    public void incrementResponsesCounter() {

        this.responsesCount.incrementAndGet();
    }


    public void incrementFallbacksCounter() {

        this.fallbacksCount.incrementAndGet();
    }


    public void measureLatency(Long published, Long now) {

        if (published == null) {
            return;
        }

        long latency = Math.max(1, now - published);

        if (latencies.size() > 144) {
            latencies.remove(0);
        }

        latencies.add(latency);
        System.out.println(latencies.stream().collect(Collectors.summarizingLong(Long::valueOf)));
    }

    public static final class Stat {

        @JsonProperty
        public String key;
        @JsonProperty
        public Object value;

        private Stat(String key, Object value) {

            this.key = key;
            this.value = value;
        }

        public static Stat of(String key, Object value) {

            return new Stat(key, value);
        }
    }
}
