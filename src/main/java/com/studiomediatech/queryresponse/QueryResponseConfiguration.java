package com.studiomediatech.queryresponse;

import com.studiomediatech.queryresponse.util.Logging;

import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


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
@ConditionalOnClass(RabbitAutoConfiguration.class)
@Import(RabbitAutoConfiguration.class)
public class QueryResponseConfiguration implements Logging {

    @Bean
    @ConditionalOnMissingBean
    DirectMessageListenerContainer directMessageListenerContainer(ConnectionFactory connectionFactory) {

        return new DirectMessageListenerContainer(connectionFactory);
    }


    @Bean
    RabbitFacade rabbitFacade(RabbitAdmin rabbitAdmin, RabbitTemplate template,
        DirectMessageListenerContainer listener, TopicExchange queriesExchange) {

        return new RabbitFacade(rabbitAdmin, template, listener, queriesExchange);
    }


    @Bean
    TopicExchange queriesExchange(@Value("${queryresponse.exchange.name:queries}") String name) {

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
}
