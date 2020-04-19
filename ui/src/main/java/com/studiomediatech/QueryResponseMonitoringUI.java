package com.studiomediatech;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.studiomediatech.queryresponse.EnableQueryResponse;
import com.studiomediatech.queryresponse.QueryBuilder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.Bean;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


@SpringBootApplication
@EnableQueryResponse
@EnableScheduling
public class QueryResponseMonitoringUI {

    public static void main(String[] args) {

        SpringApplication.run(QueryResponseMonitoringUI.class);
    }


    @Bean
    Handler handler() {

        return new Handler();
    }


    @Bean
    Querier querier(Handler handler) {

        return new Querier(handler);
    }

    static class Querier {

        private final Handler handler;

        public Querier(Handler handler) {

            this.handler = handler;
        }

        @Scheduled(fixedDelay = 1000 * 20)
        void query() {

            long countQueriesSum = QueryBuilder.queryFor("query-response/stats", Stat.class)
                    .waitingFor(1000)
                    .orEmpty()
                    .stream()
                    .peek(stat -> System.out.println("GOT: " + stat))
                    .filter(stat -> "count_queries".equals(stat.key))
                    .mapToInt(stat -> (int) stat.value)
                    .sum();

            handler.handleCountQueries(countQueriesSum);
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

            System.out.println("FUNNY THERE YOU ARE! " + session);
            sessions.add(session);
        }


        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

            System.out.println("SAD TO SEE YOU GO! " + session);
            sessions.remove(session);
        }


        public void handleCountQueries(long sum) {

            var message = String.format("{\"count_queries\": %d}", sum);
            System.out.println("HANDLING QUERIES COUNT: " + message);

            TextMessage textMessage = new TextMessage(message.getBytes());

            for (WebSocketSession s : sessions) {
                try {
                    s.sendMessage(textMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
