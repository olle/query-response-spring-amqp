package com.studiomediatech.queryresponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import com.studiomediatech.queryresponse.util.Logging;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;


/**
 * Represents a declared and registered response to some {@link Query query}, an active message listener.
 *
 * @param  <T>  type of the provided response elements.
 */
class Response<T> implements MessageListener, Logging {

    private static final ObjectWriter writer = new ObjectMapper().writer();

    private final Collection<T> elements;
    private final String queueName;
    private final String routingKey;

    private RabbitTemplate rabbitTemplate;

    protected Response(Responses<T> responses) {

        this.elements = responses.getElements();
        this.queueName = UUID.randomUUID().toString();
        this.routingKey = responses.getTerm();
    }

    @Override
    public void onMessage(Message message) {

        try {
            log().info("|--> Consumed query: " + message.getMessageProperties().getReceivedRoutingKey());

            var response = new PublishedResponseEnvelope<>(this.elements);
            log().debug("Prepared response {}", response);

            byte[] body = writer.writeValueAsBytes(response);

            var responseMessage = MessageBuilder.withBody(body)
                    .setContentEncoding(StandardCharsets.UTF_8.name())
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .build();

            var replyToAddress = message.getMessageProperties().getReplyToAddress();
            var exchangeName = replyToAddress.getExchangeName();
            var routingKey = replyToAddress.getRoutingKey();

            this.rabbitTemplate.send(exchangeName, routingKey, responseMessage);
            log().info("|<-- Published response: " + responseMessage);
        } catch (RuntimeException | JsonProcessingException e) {
            log().error("Failed to publish response message", e);
        }
    }


    static <T> Response<T> valueOf(Responses<T> responses) {

        return new Response<>(responses);
    }


    void subscribe(RabbitTemplate rabbitTemplate, AbstractMessageListenerContainer listener) {

        this.rabbitTemplate = rabbitTemplate;
        listener.setMessageListener(this);
    }


    public void accept(RabbitFacade facade) {

        this.rabbitTemplate = facade.getRabbitTemplate();
    }


    public String getQueueName() {

        return this.queueName;
    }


    public String getRoutingKey() {

        return this.routingKey;
    }

    class PublishedResponseEnvelope<R extends T> {

        @JsonProperty
        public int count;
        @JsonProperty
        public int total;
        @JsonProperty
        public Collection<R> elements = new ArrayList<>();

        PublishedResponseEnvelope(Collection<R> elements) {

            this.elements = elements;
        }

        @Override
        public String toString() {

            return "PublishedResponseEnvelope [count=" + count + ", total=" + total + "]";
        }
    }
}
