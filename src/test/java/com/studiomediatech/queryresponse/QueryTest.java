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

import java.util.Arrays;
import java.util.Collections;
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
    Statistics stats;

    QueryResponseConfigurationProperties props = new QueryResponseConfigurationProperties();

    @Mock
    AbstractMessageListenerContainer listener;

    @Captor
    ArgumentCaptor<Message> message;

    @Test
    void ensureQueryIsPublished() {

        Query<?> sut = new Query<>();
        sut.queryTerm = "term";
        sut.waitingFor = Duration.ofMillis(123);
        sut.orDefaults = Collections::emptyList;

        sut.accept(facade, stats);
        verify(facade).publishQuery(eq("term"), message.capture());

        assertThat(message.getValue()).isNotNull();
    }


    @Test
    void ensureResponseIsConsumed() {

        AtomicReference<ChainingQueryBuilder<String>> capture = new AtomicReference<>(null);

        ChainingQueryBuilder.queryFor("term", String.class)
            .withSink(capture::set)
            .waitingFor(1)
            .orEmpty();

        Query<String> sut = Query.from(capture.get(), props);
        sut.onMessage(MessageBuilder.withBody(
                    ("{'count': 3, 'total': 3, 'elements': ['foo', 'bar', 'baz']}")
                        .replaceAll("'", "\"").getBytes()).build());

        assertThat(sut.accept(facade, stats)).containsExactlyInAnyOrder("foo", "bar", "baz");
    }


    @Test
    void ensureInvokesOnErrorHandler() throws Exception {

        AtomicReference<ChainingQueryBuilder<Foo>> capture = new AtomicReference<>(null);

        CountDownLatch latch = new CountDownLatch(1);

        ChainingQueryBuilder.queryFor("term", Foo.class)
            .withSink(capture::set)
            .waitingFor(42)
            .onError(err -> {
                    assertThat(err).isInstanceOf(IllegalArgumentException.class);
                    assertThat(err.getMessage()).contains("Failed to parse response to elements of type Foo");
                    latch.countDown();
                })
            .orEmpty();

        Query<Foo> sut = Query.from(capture.get(), props);
        sut.onMessage(MessageBuilder.withBody(
                    ("{'count': 3, 'total': 3, 'elements': ['foo', 'bar', 'baz']}")
                        .replaceAll("'", "\"").getBytes()).build());

        assertThat(latch.await(123L, TimeUnit.MILLISECONDS)).isTrue();
    }


    @Test
    void ensureIgnoresErrorIfNoHandler() throws Exception {

        AtomicReference<ChainingQueryBuilder<Foo>> capture = new AtomicReference<>(null);

        ChainingQueryBuilder.queryFor("term", Foo.class)
            .withSink(capture::set)
            .waitingFor(1)
            .orEmpty();

        Query<Foo> sut = Query.from(capture.get(), props);
        sut.onMessage(MessageBuilder.withBody(
                    ("{'count': 3, 'total': 3, 'elements': ['foo', 'bar', 'baz']}")
                        .replaceAll("'", "\"").getBytes()).build());

        assertThat(sut.accept(facade, stats)).isEmpty();
    }


    @Test
    void ensureReturnsDefaultsForLessThanAtLeast() {

        AtomicReference<ChainingQueryBuilder<String>> capture = new AtomicReference<>(null);

        ChainingQueryBuilder.queryFor("foobar", String.class)
            .withSink(capture::set)
            .waitingFor(1)
            .takingAtLeast(5)
            .orDefaults(Arrays.asList("hello", "world"));

        Query<String> sut = Query.from(capture.get(), props);
        sut.onMessage(MessageBuilder.withBody(
                    ("{'count': 3, 'total': 3, 'elements': ['foo', 'bar', 'baz']}")
                        .replaceAll("'", "\"").getBytes()).build());

        assertThat(sut.accept(facade, stats)).containsExactlyInAnyOrder("hello", "world");
    }


    @Test
    void ensureReturnsOnlyAtMostWhenConsumed() {

        AtomicReference<ChainingQueryBuilder<String>> capture = new AtomicReference<>(null);

        ChainingQueryBuilder.queryFor("foobar", String.class)
            .withSink(capture::set)
            .waitingFor(1234)
            .takingAtMost(3).orEmpty();

        Query<String> sut = Query.from(capture.get(), props);
        sut.onMessage(MessageBuilder.withBody(
                    ("{'count': 2, 'total': 2, 'elements': ['hello', 'world']}")
                        .replaceAll("'", "\"").getBytes()).build());
        sut.onMessage(MessageBuilder.withBody(
                    ("{'count': 4, 'total': 4, 'elements': ['again', 'foo', 'bar', 'baz']}")
                        .replaceAll("'", "\"").getBytes()).build());

        assertThat(sut.accept(facade, stats)).containsExactly("hello", "world", "again");
    }


    @Test
    void ensureReturnsOnlyExactlyAtMostWhenConsumed() throws Exception {

        AtomicReference<ChainingQueryBuilder<String>> capture = new AtomicReference<>(null);

        ChainingQueryBuilder.queryFor("foobar", String.class)
            .withSink(capture::set)
            .waitingFor(1234)
            .takingAtMost(6).orEmpty();

        Query<String> sut = Query.from(capture.get(), props);
        sut.onMessage(MessageBuilder.withBody(
                    ("{'count': 2, 'total': 2, 'elements': ['hello', 'world']}")
                        .replaceAll("'", "\"").getBytes()).build());
        sut.onMessage(MessageBuilder.withBody(
                    ("{'count': 4, 'total': 4, 'elements': ['again', 'foo', 'bar', 'baz']}")
                        .replaceAll("'", "\"").getBytes()).build());

        assertThat(sut.accept(facade, stats)).containsExactly("hello", "world", "again", "foo", "bar", "baz");
    }


    @Test
    void ensureReturnsAllBelowAtMost() throws Exception {

        AtomicReference<ChainingQueryBuilder<String>> capture = new AtomicReference<>(null);

        ChainingQueryBuilder.queryFor("foobar", String.class)
            .withSink(capture::set)
            .waitingFor(1)
            .takingAtMost(6).orEmpty();

        Query<String> sut = Query.from(capture.get(), props);
        sut.onMessage(MessageBuilder.withBody(
                    ("{'count': 2, 'total': 2, 'elements': ['hello', 'world']}")
                        .replaceAll("'", "\"").getBytes()).build());

        assertThat(sut.accept(facade, stats)).containsExactly("hello", "world");
    }


    @Test
    void ensureThrowsWhenOrThrowsIsSetAndTimeout() throws Exception {

        AtomicReference<ChainingQueryBuilder<String>> capture = new AtomicReference<>(null);

        ChainingQueryBuilder.queryFor("foobar", String.class)
            .withSink(capture::set)
            .waitingFor(1)
            .orThrow(TimeoutOrThrowsException::new);

        Query<String> sut = Query.from(capture.get(), props);

        Assertions.assertThrows(TimeoutOrThrowsException.class, () -> sut.accept(facade, stats));
    }


    @Test
    void ensureThrowsWhenOrThrowsIsSetAndAtLeastFails() throws Exception {

        AtomicReference<ChainingQueryBuilder<String>> capture = new AtomicReference<>(null);

        ChainingQueryBuilder.queryFor("foobar", String.class)
            .withSink(capture::set)
            .waitingFor(1)
            .takingAtLeast(10)
            .orThrow(AtLeastOrThrowsException::new);

        Query<String> sut = Query.from(capture.get(), props);
        sut.onMessage(MessageBuilder.withBody(
                    ("{'count': 2, 'total': 2, 'elements': ['hello', 'world']}")
                        .replaceAll("'", "\"").getBytes()).build());
        sut.onMessage(MessageBuilder.withBody(
                    ("{'count': 4, 'total': 4, 'elements': ['again', 'foo', 'bar', 'baz']}")
                        .replaceAll("'", "\"").getBytes()).build());

        Assertions.assertThrows(AtLeastOrThrowsException.class, () -> sut.accept(facade, stats));
    }


    @Test
    void ensureSuccessWhenMoreThanAtLeastConsumed() throws Exception {

        AtomicReference<ChainingQueryBuilder<String>> capture = new AtomicReference<>(null);

        ChainingQueryBuilder.queryFor("foobar", String.class)
            .withSink(capture::set)
            .waitingFor(123).takingAtLeast(5)
            .orThrow(AtLeastOrThrowsException::new);

        Query<String> sut = Query.from(capture.get(), props);
        sut.onMessage(MessageBuilder.withBody(
                    ("{'count': 2, 'total': 2, 'elements': ['hello', 'world']}")
                        .replaceAll("'", "\"").getBytes()).build());
        sut.onMessage(MessageBuilder.withBody(
                    ("{'count': 4, 'total': 4, 'elements': ['again', 'foo', 'bar', 'baz']}")
                        .replaceAll("'", "\"").getBytes()).build());

        assertThat(sut.accept(facade, stats)).containsExactly("hello", "world", "again", "foo", "bar", "baz");
    }


    @Test
    void ensureThrowsProvidedWhenInterruptedDuringWait() throws Exception {

        AtomicReference<ChainingQueryBuilder<String>> capture = new AtomicReference<>(null);

        ChainingQueryBuilder.queryFor("foobar", String.class)
            .withSink(capture::set)
            .waitingFor(123).orThrow(AtLeastOrThrowsException::new);

        Query<String> sut = Query.from(capture.get(), props);
        sut.fail = n -> n > 100 ? true : false;

        sut.onMessage(MessageBuilder.withBody(
                    ("{'count': 2, 'total': 2, 'elements': ['hello', 'world']}")
                        .replaceAll("'", "\"").getBytes()).build());

        Assertions.assertThrows(AtLeastOrThrowsException.class, () -> sut.accept(facade, stats));
    }


    @Test
    void ensureInvokesOnErrorHandlerWhenInterruptedDuringWait() throws Exception {

        AtomicReference<ChainingQueryBuilder<String>> capture = new AtomicReference<>(null);

        ChainingQueryBuilder.queryFor("foobar", String.class)
            .withSink(capture::set)
            .waitingFor(123).onError(err -> { assertThat(err).isInstanceOf(InterruptedException.class); })
            .orEmpty();

        Query<String> sut = Query.from(capture.get(), props);
        sut.fail = n -> n > 100 ? true : false;
        sut.onMessage(MessageBuilder.withBody(
                    ("{'count': 2, 'total': 2, 'elements': ['hello', 'world']}")
                        .replaceAll("'", "\"").getBytes()).build());

        sut.accept(facade, stats);
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
