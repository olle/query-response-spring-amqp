package com.studiomediatech.queryresponse.ui;

import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.studiomediatech.queryresponse.QueryResponseTopicExchange;

@Configuration
public class ConfigureMessaging {

    public static final String QUERY_RESPONSE_STATS_QUEUE_BEAN = "queryResponseStatsQueue";
    public static final String QUERY_RESPONSE_QUERIES_QUEUE_BEAN = "queryResponseQueriesQueue";
    public static final String QUERY_RESPONSE_INTERNAL_STATS_ROUTING_KEY = "query-response/internal/stats";

    @Bean
    ConnectionNameStrategy connectionNameStrategy(Environment env) {
        return connectionFactory -> env.getProperty("spring.application.name", "query-response-ui");
    }

    @Bean(QUERY_RESPONSE_STATS_QUEUE_BEAN)
    Queue queryResponseStatsQueue() {
        return new AnonymousQueue();
    }

    @Bean
    Binding queryResponseStatsQueueBinding(QueryResponseTopicExchange queryResponseTopicExchange) {
        return BindingBuilder.bind(queryResponseStatsQueue()).to(queryResponseTopicExchange)
                .with(QUERY_RESPONSE_INTERNAL_STATS_ROUTING_KEY);
    }
}
