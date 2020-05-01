package com.studiomediatech;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.studiomediatech.queryresponse.EnableQueryResponse;
import com.studiomediatech.queryresponse.QueryBuilder;
import com.studiomediatech.queryresponse.util.Logging;

import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.core.env.Environment;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;


@SpringBootApplication
@EnableQueryResponse
@EnableScheduling
@EnableWebSocket
public class QueryResponseUI {

    public static void main(String[] args) {

        SpringApplication.run(QueryResponseUI.class);
    }

    @Configuration
    static class Config implements WebSocketConfigurer {

        @Bean
        ConnectionNameStrategy connectionNameStrategy(Environment env) {

            return connectionFactory -> env.getProperty("spring.application.name", "query-response-ui");
        }


        @Bean
        TaskScheduler taskScheduler() {

            return new ThreadPoolTaskScheduler();
        }


        @Override
        public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

            registry.addHandler(handler(), "/ws");
        }


        @Bean
        Handler handler() {

            return new Handler();
        }


        @Bean
        Querier querier(Handler handler) {

            return new Querier(handler);
        }
    }

    static class Querier {

        // This is a Fib!
        private static final int MAX_SIZE = 2584;

        static ToLongFunction<Stat> statToLong = s -> ((Number) s.value).longValue();

        private List<Stat> queries = new LinkedList<>();
        private List<Stat> responses = new LinkedList<>();

        private final Handler handler;

        public Querier(Handler handler) {

            this.handler = handler;
        }

        @Scheduled(fixedDelay = 1000 * 11)
        void query() {

            Collection<Stat> stats = QueryBuilder.queryFor("query-response/stats", Stat.class)
                    .waitingFor(1000)
                    .orEmpty();

            stats.forEach(stat -> System.out.println("GOT STAT: " + stat));

            long countQueriesSum = stats
                    .stream()
                    .filter(stat -> "count_queries".equals(stat.key))
                    .mapToLong(statToLong)
                    .sum();

            long countResponsesSum = stats
                    .stream()
                    .filter(stat -> "count_consumed_responses".equals(stat.key))
                    .mapToLong(statToLong).sum();

            long countFallbacksSum = stats
                    .stream()
                    .filter(stat -> "count_fallbacks".equals(stat.key))
                    .mapToLong(statToLong).sum();

            handler.handleCountQueriesAndResponses(countQueriesSum, countResponsesSum, countFallbacksSum);

            long minLatency = stats
                    .stream()
                    .filter(stat -> "min_latency".equals(stat.key))
                    .mapToLong(statToLong)
                    .min()
                    .orElse(0);

            long maxLatency = stats
                    .stream()
                    .filter(stat -> "max_latency".equals(stat.key))
                    .mapToLong(statToLong)
                    .max()
                    .orElse(0);

            double avgLatency = stats
                    .stream()
                    .filter(stat -> "avg_latency".equals(stat.key))
                    .mapToDouble(stat -> (double) stat.value)
                    .average()
                    .orElse(0.0d);

            handler.handleLatency(minLatency, maxLatency, avgLatency);

            // Order is important!!
            double throughputQueries = calculateThroughput("throughput_queries", stats, queries);
            double throughputResponses = calculateThroughput("throughput_responses", stats, responses);
            double throughputAvg = calculateThroughputAvg(queries, responses);

            handler.handleThroughput(throughputQueries, throughputResponses, throughputAvg);
        }


        private double calculateThroughputAvg(List<Stat> queries, List<Stat> responses) {

            List<Stat> all = new ArrayList<>();
            all.addAll(queries);
            all.addAll(responses);
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

            long sum = all.stream().mapToLong(statToLong).sum();

            return (sum * 1.0) / duration;
        }


        private double calculateThroughput(String key, Collection<Stat> source, List<Stat> dest) {

            List<Stat> ts = source
                    .stream()
                    .filter(stat -> key.equals(stat.key))
                    .sorted(Comparator.comparing(s -> s.timestamp))
                    .collect(Collectors.toList());

            for (Stat stat : ts) {
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

            long sum = dest.stream().mapToLong(statToLong).sum();

            return (sum * 1.0) / duration;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        static class Stat {

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

    static class Handler extends TextWebSocketHandler implements Logging {

        private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {

            sessions.add(session);
        }


        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

            sessions.remove(session);
        }


        public void handleCountQueriesAndResponses(long countQueriesSum, long countResponsesSum,
            long countFallbacksSum) {

            var json = String.format("{"
                    + "\"count_queries\": %d,"
                    + "\"count_responses\": %d,"
                    + "\"count_fallbacks\": %d"
                    + "}", countQueriesSum, countResponsesSum, countFallbacksSum);

            publishTextMessageWithPayload(json);
        }


        public void handleLatency(long minLatency, long maxLatency, double avgLatency) {

            var json = String.format(Locale.US,
                    "{"
                    + "\"min_latency\": %d,"
                    + "\"max_latency\": %d,"
                    + "\"avg_latency\": %f"
                    + "}", minLatency, maxLatency, avgLatency);

            publishTextMessageWithPayload(json);
        }


        public void handleThroughput(double queries, double responses, double avg) {

            var json = String.format(Locale.US,
                    "{"
                    + "\"throughput_queries\": %f,"
                    + "\"throughput_responses\": %f,"
                    + "\"avg_throughput\": %f"
                    + "}", queries, responses, avg);

            publishTextMessageWithPayload(json);
        }


        private void publishTextMessageWithPayload(String json) {

            var message = new TextMessage(json.getBytes(StandardCharsets.UTF_8));

            for (WebSocketSession s : sessions) {
                try {
                    s.sendMessage(message);
                } catch (IOException e) {
                    log().error("Could not publish text message to websocket", e);
                }
            }
        }
    }
}
