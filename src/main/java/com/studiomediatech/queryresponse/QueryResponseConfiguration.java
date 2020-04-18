package com.studiomediatech.queryresponse;

import com.studiomediatech.queryresponse.util.Logging;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import org.springframework.core.env.Environment;


/**
 * Configures the required components for a Query/Response client, ensuring the availability of the necessary AMQP
 * resources as well as a {@link QueryRegistry} and a {@link ResponseRegistry}.
 */
@Configuration
@ConditionalOnClass(RabbitAutoConfiguration.class)
@Import(RabbitAutoConfiguration.class)
class QueryResponseConfiguration implements Logging {

    @Bean
    RabbitFacade rabbitFacade(RabbitAdmin rabbitAdmin, RabbitTemplate template, ConnectionFactory connectionFactory,
        TopicExchange queriesExchange) {

        return new RabbitFacade(rabbitAdmin, template, connectionFactory, queriesExchange);
    }


    @Bean
    TopicExchange queriesExchange(@Value("${queryresponse.exchange.name:query-response}") String name) {

        return log(ExchangeBuilder.topicExchange(name).autoDelete().build());
    }


    @Bean
    ResponseRegistry respondingRegistry(RabbitFacade facade) {

        return new ResponseRegistry(facade);
    }


    @Bean
    QueryRegistry queryingRegistry(RabbitFacade facade) {

        return new QueryRegistry(facade);
    }


    @Bean
    @ConditionalOnMissingBean
    MeterRegistry meterRegistry() {

        return new SimpleMeterRegistry();
    }


    @Bean
    Statistics statistics(Environment env, ApplicationContext ctx, MeterRegistry meters) {

        return new Statistics(env, ctx, meters);
    }
}
