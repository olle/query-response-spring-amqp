package com.studiomediatech.queryresponse;

import com.studiomediatech.queryresponse.util.Logging;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Binding.DestinationType;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class RabbitFacade implements Logging {

    private final RabbitAdmin admin;
    private final ConnectionFactory connectionFactory;
    private final RabbitTemplate template;

    private TopicExchange queriesExchange;

    protected final Map<String, DirectMessageListenerContainer> containers = new ConcurrentHashMap<>();

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

        DirectMessageListenerContainer container = new DirectMessageListenerContainer(connectionFactory);
        container.setConsumersPerQueue(1);
        container.setAcknowledgeMode(AcknowledgeMode.NONE);

        return container;
    }


    public void addListener(Response<?> response) {

        createMessageListenerContainer(response, response.getQueueName());
    }


    public void addListener(Query<?> query) {

        createMessageListenerContainer(query, query.getQueueName());
    }


    public void removeListener(Query<?> query) {

        DirectMessageListenerContainer container = containers.remove(query.getQueueName());

        if (container != null) {
            container.removeQueueNames(query.getQueueName());
            container.stop();
        }
    }


    private DirectMessageListenerContainer createMessageListenerContainer(MessageListener listener, String queueName) {

        return containers.computeIfAbsent(queueName,
                key -> {
                    var container = createNewListenerContainer();

                    container.addQueueNames(key);
                    container.setMessageListener(listener);
                    container.start();

                    return container;
                });
    }


    public void removeQueue(Query<?> query) {

        admin.deleteQueue(query.getQueueName());
    }


    public RabbitTemplate getRabbitTemplate() {

        return this.template;
    }


    public void publishResponse(String exchange, String routingKey, Message message) {

        try {
            this.template.send(exchange, routingKey, message);
            log().info("|<-- Published response: " + message);
        } catch (RuntimeException e) {
            log().error("Failed to publish response", e);
        }
    }
}
