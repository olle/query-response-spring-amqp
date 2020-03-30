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

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;
import java.util.function.Supplier;


/**
 * Represents a declared and registered response to some {@link Query query}, an active message listener.
 *
 * @param  <T>  type of the provided response elements.
 */
class Response<T> implements MessageListener, Logging {

    private static final ObjectWriter writer = new ObjectMapper().writer();

    private final String queueName;
    private final String routingKey;

    private RabbitFacade facade;

    private Supplier<Iterator<T>> elements;
    private Supplier<Integer> total;

    // Visible to tests
    protected Response(String queueName, String routingKey) {

        this.queueName = queueName;
        this.routingKey = routingKey;
    }


    // Visible to tests
    protected Response(ResponseBuilder<T> responses) {

        this(UUID.randomUUID().toString(), responses.getRespondToTerm());

        this.elements = responses.elements();
        this.total = responses.total();
    }

    @Override
    public void onMessage(Message message) {

        try {
            log().info("|--> Consumed query: " + message.getMessageProperties().getReceivedRoutingKey());

            var response = buildResponse();
            log().debug("Prepared response {}", response);

            byte[] body = writer.writeValueAsBytes(response);

            var responseMessage = MessageBuilder.withBody(body)
                    .setContentEncoding(StandardCharsets.UTF_8.name())
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .build();

            var replyToAddress = message.getMessageProperties().getReplyToAddress();
            var exchangeName = replyToAddress.getExchangeName();
            var routingKey = replyToAddress.getRoutingKey();

            this.facade.publishResponse(exchangeName, routingKey, responseMessage);
        } catch (RuntimeException | JsonProcessingException e) {
            log().error("Failed to publish response message", e);
        }
    }


    private Response<T>.PublishedResponseEnvelope<T> buildResponse() {

        var response = new PublishedResponseEnvelope<T>();

        Iterator<T> it = this.elements.get();

        while (it.hasNext()) {
            response.elements.add(it.next());
        }

        response.count = response.elements.size();
        response.total = this.total.get();

        return response;
    }


    static <T> Response<T> valueOf(ResponseBuilder<T> responses) {

        return new Response<>(responses);
    }


    public void accept(RabbitFacade facade) {

        this.facade = facade;
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

        PublishedResponseEnvelope() {

            // OK
        }

        @Override
        public String toString() {

            return "PublishedResponseEnvelope [count=" + count + ", total=" + total + "]";
        }
    }
}
