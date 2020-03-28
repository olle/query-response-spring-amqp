package com.studiomediatech.queryresponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;

import java.time.Duration;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class QueryTest {

    @Mock
    RabbitFacade facade;

    @Mock
    AbstractMessageListenerContainer listener;

    @Captor
    ArgumentCaptor<Message> message;

    @Test
    void ensureQueryIsPublished() {

        var sut = new Query<>();
        sut.queryTerm = "term";
        sut.waitingFor = Duration.ofMillis(123);
        sut.orDefaults = Collections::emptyList;

        sut.accept(facade);
        verify(facade).publishQuery(eq("term"), message.capture());

        assertThat(message.getValue()).isNotNull();
    }


    @Test
    void ensureResponseIsConsumed() {

        var sut = Query.from(QueryBuilder.queryFor("term", String.class).waitingFor(42));

        var response = MessageBuilder.withBody(("{'count': 3, 'total': 3, 'elements': ['foo', 'bar', 'baz']}")
                        .replaceAll("'", "\"").getBytes()).build();
        sut.onMessage(response);

        assertThat(sut.accept(facade)).containsExactlyInAnyOrder("foo", "bar", "baz");
    }


    @Test
    void ensureInvokesOnErrorHandler() throws Exception {

        CountDownLatch latch = new CountDownLatch(1);

        var sut = Query.from(QueryBuilder.queryFor("term", Foo.class)
                .waitingFor(42)
                .onError(err -> {
                    assertThat(err).isInstanceOf(IllegalArgumentException.class);
                    assertThat(err.getMessage()).contains("Failed to parse response to elements of type Foo");
                    latch.countDown();
                }));

        var response = MessageBuilder.withBody(("{'count': 3, 'total': 3, 'elements': ['foo', 'bar', 'baz']}")
                        .replaceAll("'", "\"").getBytes()).build();
        sut.onMessage(response);

        assertThat(latch.await(3L, TimeUnit.SECONDS)).isTrue();
    }

    static class Foo {

        public String one;
        public int two;
    }
}
