package com.studiomediatech;

import com.studiomediatech.queryresponse.EnableQueryResponse;

import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.time.Instant;


@SpringBootApplication
@EnableQueryResponse
@EnableScheduling
@EnableWebSocket
public class QueryResponseUI {

    public static void main(String[] args) {

        SpringApplication.run(QueryResponseUI.class);
    }

    @Order(10)
    @Configuration
    static class AppConfig {

        @Bean
        ConnectionNameStrategy connectionNameStrategy(Environment env) {

            return connectionFactory -> env.getProperty("spring.application.name", "query-response-ui");
        }


        @Bean
        @Primary
        TaskScheduler taskScheduler() {

            return new ThreadPoolTaskScheduler();
        }


        @Bean
        WebSocketHandler handler(ApplicationEventPublisher publisher, TaskScheduler scheduler) {

            return new WebSocketHandler(event ->
                        scheduler.schedule(() -> publisher.publishEvent(event), Instant.EPOCH));
        }


        @Bean
        QueryPublisher querier(WebSocketHandler handler) {

            return new QueryPublisher(handler);
        }
    }

    @Order(100)
    @Configuration
    static class WebSocketConfig implements WebSocketConfigurer {

        private final WebSocketHandler webSocketHandler;

        public WebSocketConfig(WebSocketHandler webSocketHandler) {

            this.webSocketHandler = webSocketHandler;
        }

        @Override
        public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

            registry.addHandler(webSocketHandler, "/ws");
        }
    }
}
