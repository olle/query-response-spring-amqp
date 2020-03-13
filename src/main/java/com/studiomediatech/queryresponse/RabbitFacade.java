package com.studiomediatech.queryresponse;

import com.studiomediatech.queryresponse.util.Logging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Binding.DestinationType;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;


public class RabbitFacade implements Logging {

    /**
     * The current implementation version of Query/Response declares the globally common exchange name here, as a
     * convention.
     *
     * <p>NOTE: In a future version, the intent is of course to allow users to modify this by some means of
     * configuration.</p>
     */
    private static final String QUERIES_EXCHANGE = "queries";

    private final RabbitAdmin admin;
    private final RabbitTemplate template;
    private final DirectMessageListenerContainer listener;

    private Exchange exchange;

    public RabbitFacade(RabbitAdmin admin, RabbitTemplate template, DirectMessageListenerContainer listener) {

        this.admin = admin;
        this.template = template;
        this.listener = listener;

        declareTopicExchange();
    }

    private void declareTopicExchange() {

        this.exchange = log(ExchangeBuilder.topicExchange(QUERIES_EXCHANGE).autoDelete().build());
        admin.declareExchange(this.exchange);
    }


    public void declareQueue(Response<?> response) {

        declareQueue(response.getQueueName());
    }


    public void declareQueue(Query<?> query) {

        declareQueue(query.getQueueName());
    }


    private void declareQueue(String queueName) {

        admin.declareQueue(log(QueueBuilder.nonDurable(queueName).autoDelete().exclusive().build()));
    }


    public void declareBinding(Response<?> response) {

        admin.declareBinding(log(
                new Binding(response.getQueueName(), DestinationType.QUEUE, exchange.getName(),
                    response.getRoutingKey(), null)));
    }


    public void addListener(Response<?> response) {

        // TODO: Multiple responses per container (app/context)
        listener.addQueueNames(response.getQueueName());
        listener.setMessageListener(response);
    }


    public void addListener(Query<?> query) {

        // TODO: Multiple queries per container (app/context)
        listener.addQueueNames(query.getQueueName());
        listener.setMessageListener(query);
    }


    public void removeListener(Query<?> query) {

        if (listener.removeQueueNames(query.getQueueName())) {
            admin.deleteQueue(query.getQueueName());
        }
    }


    public RabbitTemplate getRabbitTemplate() {

        return this.template;
    }
}
