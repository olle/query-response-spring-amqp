package com.studiomediatech;

import com.studiomediatech.queries.QueryingRegistry;

import com.studiomediatech.responses.RespondingRegistry;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class QueryResponseConfiguration {

    @Bean
    @ConditionalOnMissingBean
    RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {

        return new RabbitAdmin(connectionFactory);
    }


    @Bean
    @ConditionalOnMissingBean
    DirectMessageListenerContainer directMessageListenerContainer(ConnectionFactory connectionFactory) {

        return new DirectMessageListenerContainer(connectionFactory);
    }


    @Bean
    RespondingRegistry respondingRegistry(RabbitAdmin rabbitAdmin,
        DirectMessageListenerContainer directMessageListenerContainer) {

        return new RespondingRegistry(rabbitAdmin, directMessageListenerContainer);
    }


    @Bean
    QueryingRegistry queryingRegistry(RabbitAdmin rabbitAdmin,
        DirectMessageListenerContainer directMessageListenerContainer, RabbitTemplate rabbitTemplate) {

        return new QueryingRegistry(rabbitAdmin, directMessageListenerContainer, rabbitTemplate);
    }
}
