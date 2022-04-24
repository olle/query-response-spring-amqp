package com.studiomediatech.queryresponse;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.SchedulingConfiguration;

import com.studiomediatech.queryresponse.util.Logging;


/**
 * Configures the required components for a Query/Response client, ensuring the availability of the necessary AMQP
 * resources as well as a {@link QueryRegistry} and a {@link ResponseRegistry}.
 */
@Configuration
@ConditionalOnClass(RabbitAutoConfiguration.class)
@AutoConfigureAfter(RabbitAutoConfiguration.class)
@Import({ RabbitAutoConfiguration.class, SchedulingConfiguration.class })
@EnableConfigurationProperties(QueryResponseConfigurationProperties.class)
class QueryResponseConfiguration implements Logging {

    private final QueryResponseConfigurationProperties props;

    public QueryResponseConfiguration(QueryResponseConfigurationProperties props) {

        this.props = props;
    }

    @Bean
    @ConditionalOnMissingBean
    public QueryBuilder queryBuilder(QueryRegistry registry) {

        return new QueryBuilder(registry);
    }


    @Bean
    @ConditionalOnMissingBean
    public ResponseBuilder responseBuilder(ResponseRegistry registry) {

        return new ResponseBuilder(registry);
    }


    @Bean
    @ConditionalOnMissingBean
    RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {

        return new RabbitAdmin(connectionFactory);
    }


    @Bean
    RabbitFacade rabbitFacade(RabbitAdmin rabbitAdmin, ConnectionFactory connectionFactory,
        TopicExchange queriesExchange, GenericApplicationContext ctx) {

        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

        return new RabbitFacade(rabbitAdmin, rabbitTemplate, connectionFactory, queriesExchange, ctx);
    }


    @Bean
    QueryResponseTopicExchange queryResponseTopicExchange() {

        return log(new QueryResponseTopicExchange(props.getExchange().getName()));
    }


    @Bean
    Statistics statistics(Environment env, ApplicationContext ctx, RabbitFacade facade) {

        return new Statistics(env, ctx, facade, props);
    }


    @Bean
    ResponseRegistry responseRegistry(RabbitFacade facade, Statistics stats) {

        return new ResponseRegistry(facade, stats, props);
    }


    @Bean
    QueryRegistry queryRegistry(RabbitFacade facade, Statistics stats) {

        return new QueryRegistry(facade, stats, props);
    }
}
