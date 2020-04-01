package com.studiomediatech.queryresponse;

import org.junit.jupiter.api.Assertions;
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
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

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

        AtomicReference<QueryBuilder<String>> capture = new AtomicReference<>(null);

        QueryBuilder.queryFor("term", String.class)
            .withSink(capture::set)
            .waitingFor(1)
            .orEmpty();

        var sut = Query.from(capture.get());
        sut.onMessage(MessageBuilder.withBody(
                    ("{'count': 3, 'total': 3, 'elements': ['foo', 'bar', 'baz']}")
                        .replaceAll("'", "\"").getBytes()).build());

        assertThat(sut.accept(facade)).containsExactlyInAnyOrder("foo", "bar", "baz");
    }


    @Test
    void ensureInvokesOnErrorHandler() throws Exception {

        AtomicReference<QueryBuilder<Foo>> capture = new AtomicReference<>(null);

        CountDownLatch latch = new CountDownLatch(1);

        QueryBuilder.queryFor("term", Foo.class)
            .withSink(capture::set)
            .waitingFor(42)
            .onError(err -> {
                    assertThat(err).isInstanceOf(IllegalArgumentException.class);
                    assertThat(err.getMessage()).contains("Failed to parse response to elements of type Foo");
                    latch.countDown();
                })
            .orEmpty();

        var sut = Query.from(capture.get());
        sut.onMessage(MessageBuilder.withBody(
                    ("{'count': 3, 'total': 3, 'elements': ['foo', 'bar', 'baz']}")
                        .replaceAll("'", "\"").getBytes()).build());

        assertThat(latch.await(123L, TimeUnit.MILLISECONDS)).isTrue();
    }


    @Test
    void ensureIgnoresErrorIfNoHandler() throws Exception {

        AtomicReference<QueryBuilder<Foo>> capture = new AtomicReference<>(null);

        QueryBuilder.queryFor("term", Foo.class)
            .withSink(capture::set)
            .waitingFor(1)
            .orEmpty();

        var sut = Query.from(capture.get());
        sut.onMessage(MessageBuilder.withBody(
                    ("{'count': 3, 'total': 3, 'elements': ['foo', 'bar', 'baz']}")
                        .replaceAll("'", "\"").getBytes()).build());

        assertThat(sut.accept(facade)).isEmpty();
    }


    @Test
    void ensureReturnsDefaultsForLessThanAtLeast() {

        AtomicReference<QueryBuilder<String>> capture = new AtomicReference<>(null);

        QueryBuilder.queryFor("foobar", String.class)
            .withSink(capture::set)
            .waitingFor(1)
            .takingAtLeast(5)
            .orDefaults(List.of("hello", "world"));

        var sut = Query.from(capture.get());
        sut.onMessage(MessageBuilder.withBody(
                    ("{'count': 3, 'total': 3, 'elements': ['foo', 'bar', 'baz']}")
                        .replaceAll("'", "\"").getBytes()).build());

        assertThat(sut.accept(facade)).containsExactlyInAnyOrder("hello", "world");
    }


    @Test
    void ensureReturnsOnlyAtMostWhenConsumed() {

        AtomicReference<QueryBuilder<String>> capture = new AtomicReference<>(null);

        QueryBuilder.queryFor("foobar", String.class)
            .withSink(capture::set)
            .waitingFor(1234)
            .takingAtMost(3)
            .orEmpty();

        var sut = Query.from(capture.get());
        sut.onMessage(MessageBuilder.withBody(
                    ("{'count': 2, 'total': 2, 'elements': ['hello', 'world']}")
                        .replaceAll("'", "\"").getBytes()).build());
        sut.onMessage(MessageBuilder.withBody(
                    ("{'count': 4, 'total': 4, 'elements': ['again', 'foo', 'bar', 'baz']}")
                        .replaceAll("'", "\"").getBytes()).build());

        assertThat(sut.accept(facade)).containsExactly("hello", "world", "again");
    }


    @Test
    void ensureThrowsWhenOrThrowsIsSetAndTimeout() throws Exception {

        AtomicReference<QueryBuilder<String>> capture = new AtomicReference<>(null);

        QueryBuilder.queryFor("foobar", String.class)
            .withSink(capture::set)
            .waitingFor(1)
            .orThrow(TimeoutOrThrowsException::new);

        var sut = Query.from(capture.get());

        Assertions.assertThrows(TimeoutOrThrowsException.class, () -> sut.accept(facade));
    }


    @Test
    void ensureThrowsWhenOrThrowsIsSetAndAtLeastFails() throws Exception {

        AtomicReference<QueryBuilder<String>> capture = new AtomicReference<>(null);

        QueryBuilder.queryFor("foobar", String.class)
            .withSink(capture::set)
            .waitingFor(1)
            .takingAtLeast(10)
            .orThrow(AtLeastOrThrowsException::new);

        var sut = Query.from(capture.get());
        sut.onMessage(MessageBuilder.withBody(
                    ("{'count': 2, 'total': 2, 'elements': ['hello', 'world']}")
                        .replaceAll("'", "\"").getBytes()).build());
        sut.onMessage(MessageBuilder.withBody(
                    ("{'count': 4, 'total': 4, 'elements': ['again', 'foo', 'bar', 'baz']}")
                        .replaceAll("'", "\"").getBytes()).build());

        Assertions.assertThrows(AtLeastOrThrowsException.class, () -> sut.accept(facade));
    }

    static class TimeoutOrThrowsException extends RuntimeException {

        private static final long serialVersionUID = 1L;
    }

    static class AtLeastOrThrowsException extends RuntimeException {

        private static final long serialVersionUID = 1L;
    }

    static class Foo {

        public String one;
        public int two;
    }
}
