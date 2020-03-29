package com.studiomediatech.queryresponse;

import com.studiomediatech.queryresponse.util.Logging;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Binding.DestinationType;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Provides an abstraction between the use of RabbitMQ and the capabilities in Spring Boot AMQP, and the client code in
 * this library.
 */
class RabbitFacade implements Logging {

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


    /**
     * Publishes a query to the Query/Response exchange, with the given routing key and message.
     *
     * @param  routingKey  query, or query-term to publish
     * @param  message  to publish
     *
     * @throws  RuntimeException  if publishing failed
     */
    public void publishQuery(String routingKey, Message message) {

        var m = decorateMessage(message);

        this.template.send(queriesExchange.getName(), routingKey, m);
        log().info("|<-- Published query: {} - {}", routingKey, m);
    }


    /**
     * Publishes a response to the given exchange and routing key, with the provided response message.
     *
     * <p>Any {@link RuntimeException} failures are caught, logged and ignored.</p>
     *
     * @param  exchange  address
     * @param  routingKey  address
     * @param  message  to publish
     */
    public void publishResponse(String exchange, String routingKey, Message message) {

        var m = decorateMessage(message);

        try {
            this.template.send(exchange, routingKey, m);
            log().info("|<-- Published response: " + m);
        } catch (RuntimeException e) {
            log().error("Failed to publish response", e);
        }
    }


    private Message decorateMessage(Message message) {

        return MessageBuilder.fromMessage(message)
            .setDeliveryMode(MessageDeliveryMode.NON_PERSISTENT)
            .setContentType(MessageProperties.CONTENT_TYPE_JSON)
            .setContentLength(message.getBody().length)
            .build();
    }
}
