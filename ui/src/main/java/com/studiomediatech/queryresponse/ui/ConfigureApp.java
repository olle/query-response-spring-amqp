package com.studiomediatech.queryresponse.ui;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.studiomediatech.QueryPublisher;
import com.studiomediatech.queryresponse.QueryBuilder;
import com.studiomediatech.queryresponse.ui.api.WebSocketApiHandlerPort;
import com.studiomediatech.queryresponse.ui.app.adapter.EventEmitterAdapter;
import com.studiomediatech.queryresponse.ui.infra.AsyncEventEmitter;

@Order(10)
@Configuration
class ConfigureApp {

    @Bean
    WebSocketApiHandlerPort handler(EventEmitterAdapter emitter) {

        return new WebSocketApiHandlerPort(emitter);
    }

    @Bean
    QueryPublisher querier(WebSocketApiHandlerPort handler, QueryBuilder queryBuilder) {

        return new QueryPublisher(handler, queryBuilder);
    }

}