package com.studiomediatech.queryresponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.nio.charset.StandardCharsets;

import java.util.Collection;


public class Responding<T> implements MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(Responding.class);

    private final RabbitTemplate rabbitTemplate;
    private final Responses<T> responses;
    private final ObjectMapper objectMapper;

    public Responding(RabbitTemplate rabbitTemplate, Responses<T> responses) {

        this.rabbitTemplate = rabbitTemplate;
        this.responses = responses;

        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void onMessage(Message message) {

        try {
            LOG.info("|--> Consumed query: " + message.getMessageProperties().getReceivedRoutingKey());

            var replyToAddress = message.getMessageProperties().getReplyToAddress();

            var response = new ResponseV1();
            response.elements = responses.getElements();
            response.count = response.elements.size();
            response.total = response.elements.size();

            byte[] body = objectMapper.writeValueAsBytes(response);

            var responseMessage = MessageBuilder.withBody(body)
                    .setContentEncoding(StandardCharsets.UTF_8.name())
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .build();

            rabbitTemplate.send(replyToAddress.getExchangeName(), replyToAddress.getRoutingKey(), responseMessage);
            LOG.info("|<-- Published response: " + responseMessage);
        } catch (RuntimeException | JsonProcessingException e) {
            LOG.error("Failed to publish response message", e);
        }
    }

    static class ResponseV1 {

        @JsonProperty
        public int count;
        @JsonProperty
        public int total;
        @SuppressWarnings("rawtypes")
        @JsonProperty
        public Collection elements;
    }
}
