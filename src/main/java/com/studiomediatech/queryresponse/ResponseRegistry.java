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

import com.studiomediatech.queryresponse.util.Logging;

import java.util.function.Supplier;


/**
 * Provides a way to create and register responses.
 */
class ResponseRegistry implements ApplicationContextAware, Logging {

    // WARNING: Non-exemplary use of static supplier, for lazy access to bean instance.
    protected static Supplier<ResponseRegistry> instance = () -> null;

    private final RabbitAdmin rabbitAdmin;
    private final DirectMessageListenerContainer listener;
    private final RabbitTemplate rabbitTemplate;

    public ResponseRegistry(RabbitAdmin rabbitAdmin, DirectMessageListenerContainer listener,
        RabbitTemplate rabbitTemplate) {

        this.rabbitAdmin = rabbitAdmin;
        this.listener = listener;
        this.rabbitTemplate = rabbitTemplate;
    }

    public static <T> void register(Responses<T> responses) {

        var registry = instance.get();

        if (registry == null) {
            throw new IllegalStateException("No registry is initialized.");
        }

        registry.accept(responses);
    }


    protected <T> void accept(Responses<T> responses) {

        var exchange = declareQueriesExchange();
        var queue = rabbitAdmin.declareQueue();

        rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(responses.getTerm()).noargs());

        listener.addQueueNames(queue.getActualName());

        Response<T> responder = new Response<>(rabbitTemplate, responses);
        listener.setMessageListener(responder);

        log().info("Added responder {} to registry {} ", responses, instance.get());
    }


    private Exchange declareQueriesExchange() {

        var exchange = ExchangeBuilder.topicExchange("queries").autoDelete().build();
        rabbitAdmin.declareExchange(exchange);

        return exchange;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        ResponseRegistry.instance = () -> applicationContext.getBean(ResponseRegistry.class);
    }
}
