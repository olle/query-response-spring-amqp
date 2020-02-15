package com.studiomediatech.queries;

import com.studiomediatech.Query;
import com.studiomediatech.Response;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;

import org.springframework.beans.BeansException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.function.Supplier;


public class QueryingRegistry implements ApplicationContextAware {

    // WARNING: Non-exemplary use of static supplier, for lazy access to bean instance.
    protected static Supplier<QueryingRegistry> instance = () -> null;

    private final RabbitAdmin rabbitAdmin;
    private final DirectMessageListenerContainer listener;
    private final RabbitTemplate rabbitTemplate;

    public QueryingRegistry(RabbitAdmin admin, DirectMessageListenerContainer listener, RabbitTemplate rabbit) {

        this.rabbitAdmin = admin;
        this.listener = listener;
        this.rabbitTemplate = rabbit;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        QueryingRegistry.instance = () -> applicationContext.getBean(QueryingRegistry.class);
    }


    public static <T> Response<T> register(Query<T> query, Response<T> orDefault) {

        var registry = instance.get();

        if (registry == null) {
            throw new IllegalStateException("No registry is initialized.");
        }

        return registry._register(query, orDefault);
    }


    protected <T> Response<T> _register(Query<T> query, Response<T> orDefault) {

        var exchange = declareQueriesExchange();
        var queue = rabbitAdmin.declareQueue();

        listener.addQueueNames(queue.getActualName());

        var q = new Querent<>(query, orDefault);
        listener.setMessageListener(q);

        return q.publish(rabbitTemplate);
    }


    private Exchange declareQueriesExchange() {

        var exchange = ExchangeBuilder.topicExchange("queries").autoDelete().build();
        rabbitAdmin.declareExchange(exchange);

        return exchange;
    }
}
