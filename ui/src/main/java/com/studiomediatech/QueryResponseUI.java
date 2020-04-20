package com.studiomediatech;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.studiomediatech.queryresponse.EnableQueryResponse;
import com.studiomediatech.queryresponse.QueryBuilder;

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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


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

        private final Handler handler;

        public Querier(Handler handler) {

            this.handler = handler;
        }

        @Scheduled(fixedDelay = 1000 * 11)
        void query() {

            Collection<Stat> stats = QueryBuilder.queryFor("query-response/stats", Stat.class)
                    .waitingFor(1000)
                    .orEmpty();

            long countQueriesSum = stats
                    .stream()
                    .filter(stat -> "count_queries".equals(stat.key))
                    .mapToInt(stat -> (int) stat.value)
                    .sum();

            long countResponsesSum = stats
                    .stream()
                    .filter(stat -> "count_responses".equals(stat.key))
                    .mapToInt(stat -> (int) stat.value)
                    .sum();

            handler.handleCountQueriesAndResponses(countQueriesSum, countResponsesSum);
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        static class Stat {

            @JsonProperty
            public String key;
            @JsonProperty
            public Object value;

            @Override
            public String toString() {

                return key + "=" + value;
            }
        }
    }

    static class Handler extends TextWebSocketHandler {

        long lastCountQueries = -1;
        long countQueries = -1;

        private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {

            sessions.add(session);
        }


        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

            sessions.remove(session);
        }


        public void handleCountQueriesAndResponses(long countQueriesSum, long countResponsesSum) {

            var json = String.format("{"
                    + "\"count_queries\": %d,"
                    + "\"count_responses\": %d"
                    + "}", countQueriesSum, countResponsesSum);

            var message = new TextMessage(json.getBytes(StandardCharsets.UTF_8));

            for (WebSocketSession s : sessions) {
                try {
                    s.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
