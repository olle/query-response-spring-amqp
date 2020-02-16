package com.studiomediatech.queryresponse;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;

import org.springframework.beans.BeansException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.function.Supplier;
import java.util.stream.Stream;


class RespondingRegistry implements ApplicationContextAware {

    // WARNING: Non-exemplary use of static supplier, for lazy access to bean instance.
    protected static Supplier<RespondingRegistry> instance = () -> null;

    private final RabbitAdmin rabbitAdmin;
    private final DirectMessageListenerContainer listener;
    private final RabbitTemplate rabbitTemplate;

    public RespondingRegistry(RabbitAdmin rabbitAdmin, DirectMessageListenerContainer listener,
        RabbitTemplate rabbitTemplate) {

        this.rabbitAdmin = rabbitAdmin;
        this.listener = listener;
        this.rabbitTemplate = rabbitTemplate;
    }

    public static <T> void register(Responses<T> responses, Stream<T> stream) {

        var registry = instance.get();

        if (registry == null) {
            throw new IllegalStateException("No registry is initialized.");
        }

        registry._register(responses, stream);

        System.out.println("Adding responses " + responses + " to registry " + registry);
    }


    protected <T> void _register(Responses<T> responses, Stream<T> stream) {

        var exchange = declareQueriesExchange();
        var queue = rabbitAdmin.declareQueue();

        rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(responses.getTerm()).noargs());

        listener.addQueueNames(queue.getActualName());

        Responding<T> responder = new Responding<>(rabbitTemplate, responses);
        listener.setMessageListener(responder);
    }


    private Exchange declareQueriesExchange() {

        var exchange = ExchangeBuilder.topicExchange("queries").autoDelete().build();
        rabbitAdmin.declareExchange(exchange);

        return exchange;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        RespondingRegistry.instance = () -> applicationContext.getBean(RespondingRegistry.class);
    }
}
