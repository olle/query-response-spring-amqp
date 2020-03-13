package com.studiomediatech.queryresponse;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Configures the required components for a Query/Response client, ensuring the availability of the necessary AMQP
 * resources as well as a {@link QueryRegistry} and a {@link ResponseRegistry}.
 *
 * <p>This configuration can be used to enable Query/Response in Spring and Spring Boot applications, simply by
 * importing it in the application starter class.</p>
 *
 * <pre>
      {@literal @}SpringBootApplication
      {@literal @}Import(QueryResponseConfiguration.class)
      public class MyApplication {
          // ...
      }
 * </pre>
 */
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
    ResponseRegistry respondingRegistry(RabbitAdmin rabbitAdmin,
        DirectMessageListenerContainer directMessageListenerContainer, RabbitTemplate rabbitTemplate) {

        return new ResponseRegistry(rabbitAdmin, directMessageListenerContainer, rabbitTemplate);
    }


    @Bean
    QueryRegistry queryingRegistry(RabbitAdmin rabbitAdmin,
        DirectMessageListenerContainer directMessageListenerContainer, RabbitTemplate rabbitTemplate) {

        return new QueryRegistry(rabbitAdmin, directMessageListenerContainer, rabbitTemplate);
    }
}
