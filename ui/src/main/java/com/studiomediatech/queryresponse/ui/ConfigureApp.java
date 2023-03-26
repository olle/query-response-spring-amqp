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
import com.studiomediatech.queryresponse.ui.api.WebSocketApiHandlerPort;

@Order(10)
@Configuration
class ConfigureApp {

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
    WebSocketApiHandlerPort handler(EventEmitter emitter) {

        return new WebSocketApiHandlerPort(emitter);
    }

    @Bean
    QueryPublisher querier(WebSocketApiHandlerPort handler, QueryBuilder queryBuilder) {

        return new QueryPublisher(handler, queryBuilder);
    }

}