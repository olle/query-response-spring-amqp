package com.studiomediatech;

import com.studiomediatech.queryresponse.EnableQueryResponse;
import com.studiomediatech.queryresponse.QueryBuilder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.Bean;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


@SpringBootApplication
@EnableQueryResponse
@EnableScheduling
class QueryResponseMonitoringUI {

    public static void main(String[] args) {

        SpringApplication.run(QueryResponseMonitoringUI.class);
    }


    @Scheduled(fixedDelay = 1000 * 20)
    void query() {

        QueryBuilder.queryFor("query-response/stats", Object.class)
            .waitingFor(1000)
            .orEmpty()
            .forEach(obj -> System.out.println("Got this: " + obj));
    }


    @Bean
    Handler handler() {

        return new Handler();
    }

    static class Handler extends TextWebSocketHandler {

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {

            System.out.println("FUNNY THERE YOU ARE! " + session);
        }


        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

            System.out.println("SAD TO SEE YOU GO! " + session);
        }
    }
}
