package com.studiomediatech.queryresponse;

import com.studiomediatech.queryresponse.util.Logging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Binding.DestinationType;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;


public class RabbitFacade implements Logging {

    private final RabbitAdmin admin;
    private final RabbitTemplate template;
    private final DirectMessageListenerContainer listener;

    private TopicExchange queriesExchange;

    public RabbitFacade(RabbitAdmin admin, RabbitTemplate template, DirectMessageListenerContainer listener,
        TopicExchange queriesExchange) {

        this.admin = admin;
        this.template = template;
        this.listener = listener;
        this.queriesExchange = queriesExchange;
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
                new Binding(response.getQueueName(), DestinationType.QUEUE, queriesExchange.getName(),
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

        listener.removeQueueNames(query.getQueueName());
        admin.deleteQueue(query.getQueueName());
    }


    public RabbitTemplate getRabbitTemplate() {

        return this.template;
    }
}
