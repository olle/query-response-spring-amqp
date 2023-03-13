package com.studiomediatech.queryresponse.ui;

import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.studiomediatech.QueryPublisher;
import com.studiomediatech.events.AsyncEventEmitter;
import com.studiomediatech.events.EventEmitter;
import com.studiomediatech.queryresponse.QueryBuilder;
import com.studiomediatech.queryresponse.QueryResponseTopicExchange;
import com.studiomediatech.queryresponse.ui.api.WebSocketApiHandler;

@Order(10)
@Configuration
class ConfigureApp {

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

    @Bean(QueryResponseUIApp.QUERY_RESPONSE_STATS_QUEUE_BEAN)
    Queue queryResponseStatsQueue() {

        return new AnonymousQueue();
    }

    @Bean
    Binding queryResponseStatsQueueBinding(QueryResponseTopicExchange queryResponseTopicExchange) {

        return BindingBuilder.bind(queryResponseStatsQueue()).to(queryResponseTopicExchange)
                .with("query-response/internal/stats");
    }
}