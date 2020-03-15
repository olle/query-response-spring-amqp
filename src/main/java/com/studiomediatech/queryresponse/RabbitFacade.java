package com.studiomediatech.queryresponse;

import com.studiomediatech.queryresponse.util.Logging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Binding.DestinationType;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;


public class RabbitFacade implements Logging {

    private final RabbitAdmin admin;
    private final ConnectionFactory connectionFactory;
    private final RabbitTemplate template;

    private TopicExchange queriesExchange;

    public RabbitFacade(RabbitAdmin admin, RabbitTemplate template, ConnectionFactory connectionFactory,
        TopicExchange queriesExchange) {

        this.admin = admin;
        this.template = template;
        this.connectionFactory = connectionFactory;
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


    private DirectMessageListenerContainer createNewListenerContainer() {

        return new DirectMessageListenerContainer(connectionFactory);
    }


    public DirectMessageListenerContainer createMessageListenerContainer(Response<?> response) {

        var listener = createNewListenerContainer();
        listener.addQueueNames(response.getQueueName());
        listener.setMessageListener(response);
        listener.start();

        return listener;
    }


    public DirectMessageListenerContainer createMessageListenerContainer(Query<?> query) {

        var listener = createNewListenerContainer();
        listener.addQueueNames(query.getQueueName());
        listener.setMessageListener(query);
        listener.start();

        return listener;
    }


    public void removeQueue(Query<?> query) {

        admin.deleteQueue(query.getQueueName());
    }


    public RabbitTemplate getRabbitTemplate() {

        return this.template;
    }
}
