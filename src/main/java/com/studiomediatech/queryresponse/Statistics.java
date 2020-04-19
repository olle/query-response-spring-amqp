package com.studiomediatech.queryresponse;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.studiomediatech.queryresponse.util.Logging;

import io.micrometer.core.instrument.MeterRegistry;

import org.springframework.context.ApplicationContext;

import org.springframework.core.env.Environment;

import org.springframework.util.StringUtils;

import java.lang.management.ManagementFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.time.Duration;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;


class Statistics implements Logging {

    private static final String STAT_UPTIME = "uptime";
    private static final String STAT_PID = "pid";
    private static final String STAT_HOSTNAME = "hostname";
    private static final String STAT_NAME = "name";
    private static final String STAT_COUNT_QUERIES = "count_queries";

    private final Environment env;
    private final ApplicationContext ctx;

    private AtomicLong queriesCount = new AtomicLong(0);

    public Statistics(Environment env, ApplicationContext ctx, MeterRegistry meters) {

        this.env = env;
        this.ctx = ctx;

        ResponseBuilder.respondTo("query-response/stats", Stat.class)
            .withAll()
            .suppliedBy(this::getStats);
    }

    private Collection<Stat> getStats() {

        return List.of( // NOSONAR
                Stat.of(STAT_COUNT_QUERIES, this.queriesCount.get()), // NOSONAR
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
