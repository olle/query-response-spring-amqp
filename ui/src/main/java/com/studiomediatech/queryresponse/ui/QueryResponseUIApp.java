package com.studiomediatech.queryresponse.ui;

import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
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

import com.studiomediatech.QueryPublisher;
import com.studiomediatech.events.AsyncEventEmitter;
import com.studiomediatech.events.EventEmitter;
import com.studiomediatech.queryresponse.EnableQueryResponse;
import com.studiomediatech.queryresponse.QueryBuilder;
import com.studiomediatech.queryresponse.QueryResponseTopicExchange;
import com.studiomediatech.queryresponse.ui.api.WebSocketApiHandler;


@SpringBootApplication
@EnableQueryResponse
@EnableScheduling
@EnableWebSocket
public class QueryResponseUIApp {

	public static final String QUERY_RESPONSE_STATS_QUEUE_BEAN = "queryResponseStatsQueue";
	
    public static void main(String[] args) {

        SpringApplication.run(QueryResponseUIApp.class);
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
        EventEmitter eventEmitter(TaskScheduler scheduler, ApplicationEventPublisher publisher) {

            return new AsyncEventEmitter(scheduler, publisher);
        }


        @Bean
        WebSocketApiHandler handler(EventEmitter emitter) {

            return new WebSocketApiHandler(emitter);
        }


        @Bean
        QueryPublisher querier(WebSocketApiHandler handler, QueryBuilder queryBuilder) {

            return new QueryPublisher(handler, queryBuilder);
        }
        
        @Bean(QUERY_RESPONSE_STATS_QUEUE_BEAN)
        Queue queryResponseStatsQueue() {
        	
        	return new AnonymousQueue();
        }
        
        @Bean
        Binding queryResponseStatsQueueBinding(QueryResponseTopicExchange queryResponseTopicExchange) {
        	
        	return BindingBuilder.bind(queryResponseStatsQueue()).to(queryResponseTopicExchange).with("query-response/internal/stats");
        }
    }

    @Order(100)
    @Configuration
    static class WebSocketConfig implements WebSocketConfigurer {

        private final WebSocketApiHandler webSocketHandler;

        public WebSocketConfig(WebSocketApiHandler webSocketHandler) {

            this.webSocketHandler = webSocketHandler;
        }

        @Override
        public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

            registry.addHandler(webSocketHandler, "/ws");
        }
    }
}
