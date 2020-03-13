package com.studiomediatech.queryresponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class QueryTest {

    @Mock
    RabbitTemplate rabbit;

    @Mock
    AbstractMessageListenerContainer listener;

    @Captor
    ArgumentCaptor<Message> message;

    @Test
    void ensureQueryIsPublished() {

        var sut = Query.valueOf(Queries.queryFor("term", String.class).waitingFor(42));
        sut.orDefaults = Collections::emptyList;

        sut.publish(rabbit, "queue-name", listener);

        verify(rabbit).send(eq("queries"), eq("term"), message.capture());
        assertThat(message.getValue().getMessageProperties().getReplyTo()).isEqualTo("queue-name");
    }


    @Test
    void ensureResponseIsConsumed() throws Exception {

        var sut = Query.valueOf(Queries.queryFor("term", String.class).waitingFor(42));

        var response = MessageBuilder.withBody(("{'count': 3, 'total': 3, 'elements': ['foo', 'bar', 'baz']}")
                        .replaceAll("'", "\"").getBytes()).build();
        sut.onMessage(response);

        assertThat(sut.publish(rabbit, "queue-name", listener)).containsExactlyInAnyOrder("foo", "bar", "baz");
    }
}
