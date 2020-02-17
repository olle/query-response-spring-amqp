package com.studiomediatech.queryresponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;


public class Responding<T> implements MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(Responding.class);

    private final RabbitTemplate rabbitTemplate;

    public Responding(RabbitTemplate rabbitTemplate, Responses<T> responses) {

        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void onMessage(Message message) {

        LOG.info("|--> Consumed query: " + message.getMessageProperties().getReceivedRoutingKey());

        var replyToAddress = message.getMessageProperties().getReplyToAddress();
        var response = MessageBuilder.withBody("HELO".getBytes()).build();

        rabbitTemplate.send(replyToAddress.getExchangeName(), replyToAddress.getRoutingKey(), response);
        LOG.info("|<-- Published response: " + response);
    }
}
