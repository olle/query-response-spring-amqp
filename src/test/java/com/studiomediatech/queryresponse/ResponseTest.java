package com.studiomediatech.queryresponse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class ResponseTest {

    @Mock
    RabbitTemplate rabbit;

    @Mock
    AbstractMessageListenerContainer listener;

    @Captor
    ArgumentCaptor<Message> message;

    @Test
    @DisplayName("after consuming a query message, a response is published")
    void ensurePublishesResponseOnConsumedQueryMessage() throws Exception {

        var sut = Response.create(new Responses<>("query-term"));
        sut.subscribe(rabbit, listener);

        var query = MessageBuilder.withBody("{}".getBytes()).setReplyTo("reply-to").build();
        sut.onMessage(query);

        verify(rabbit).send(Mockito.eq(""), Mockito.eq("reply-to"), message.capture());
        assertThat(message.getValue()).isNotNull();
    }
}
