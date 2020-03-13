package com.studiomediatech.queryresponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.Collection;


/**
 * Represents a declared and registered response to some {@link Query query}, an active message listener.
 *
 * @param  <T>  type of the provided response elements.
 */
class Response<T> implements MessageListener, Logging {

    private static final ObjectWriter writer = new ObjectMapper().writer();

    private final RabbitTemplate rabbitTemplate;
    private final Responses<T> responses;

    public Response(RabbitTemplate rabbitTemplate, Responses<T> responses) {

        this.rabbitTemplate = rabbitTemplate;
        this.responses = responses;
    }

    @Override
    public void onMessage(Message message) {

        try {
            log().info("|--> Consumed query: " + message.getMessageProperties().getReceivedRoutingKey());

            var response = new PublishedResponseEnvelope<>(responses.getElements());

            byte[] body = writer.writeValueAsBytes(response);

            var responseMessage = MessageBuilder.withBody(body)
                    .setContentEncoding(StandardCharsets.UTF_8.name())
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .build();

            var replyToAddress = message.getMessageProperties().getReplyToAddress();
            var exchangeName = replyToAddress.getExchangeName();
            var routingKey = replyToAddress.getRoutingKey();

            rabbitTemplate.send(exchangeName, routingKey, responseMessage);
            log().info("|<-- Published response: " + responseMessage);
        } catch (RuntimeException | JsonProcessingException e) {
            log().error("Failed to publish response message", e);
        }
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
