package com.studiomediatech.queryresponse;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;


class QueryBuilderTest {

    @Test
    void ensureWithSinkCapturesBuilder() throws Exception {

        AtomicReference<QueryBuilder<String>> capture = new AtomicReference<>(null);

        var nope = QueryBuilder.queryFor("foobar", String.class)
                .withSink(capture::set)
                .waitingFor(123)
                .orEmpty();

        assertThat(capture.get()).isNotNull();
        assertThat(nope).isNull();
    }


    @Test
    void ensureBuilderHoldsQueryAndType() throws Exception {

        AtomicReference<QueryBuilder<String>> capture = new AtomicReference<>(null);

        QueryBuilder.queryFor("foobar", String.class)
            .withSink(capture::set)
            .waitingFor(1337)
            .orEmpty();

        assertThat(capture.get().getType()).isEqualTo(String.class);
        assertThat(capture.get().getQueryForTerm()).isEqualTo("foobar");
    }


    @Test
    void ensureBuilderCapturesWaitingForCorrectMillis() throws Exception {

        AtomicReference<QueryBuilder<String>> capture = new AtomicReference<>(null);

        QueryBuilder.queryFor("foobar", String.class)
            .withSink(capture::set)
            .waitingFor(1337)
            .orEmpty();

        assertThat(capture.get().getWaitingFor()).isEqualTo(Duration.ofMillis(1337));
    }


    @Test
    void ensureBuilderCapturesWaitingForCorrectDurationArgs() throws Exception {

        AtomicReference<QueryBuilder<String>> capture = new AtomicReference<>(null);

        QueryBuilder.queryFor("foobar", String.class)
            .withSink(capture::set)
            .waitingFor(3, ChronoUnit.SECONDS)
            .orEmpty();

        assertThat(capture.get().getWaitingFor()).isEqualTo(Duration.ofSeconds(3));
    }


    @Test
    void ensureBuilderCapturesWaitingForCorrectAsDuration() throws Exception {

        AtomicReference<QueryBuilder<String>> capture = new AtomicReference<>(null);

        QueryBuilder.queryFor("foobar", String.class)
            .withSink(capture::set)
            .waitingFor(Duration.ofMillis(3000))
            .orEmpty();

        assertThat(capture.get().getWaitingFor()).isEqualTo(Duration.ofSeconds(3));
    }


    @Test
    void ensureBuilderCapturesTakingAtMost() throws Exception {

        AtomicReference<QueryBuilder<String>> capture = new AtomicReference<>(null);

        QueryBuilder.queryFor("foobar", String.class)
            .withSink(capture::set)
            .takingAtMost(42)
            .waitingFor(123)
            .orEmpty();

        assertThat(capture.get().getTakingAtMost()).isEqualTo(42);
    }


    @Test
    void ensureBuilderCaptureTakingAtLeast() throws Exception {

        AtomicReference<QueryBuilder<String>> capture = new AtomicReference<>(null);

        QueryBuilder.queryFor("foobar", String.class)
            .withSink(capture::set)
            .takingAtLeast(33)
            .waitingFor(123)
            .orEmpty();

        assertThat(capture.get().getTakingAtLeast()).isEqualTo(33);
    }


    @Test
    void ensureBuilderCapturesOnErrorHandler() throws Exception {

        AtomicReference<QueryBuilder<String>> capture = new AtomicReference<>(null);

        Consumer<Throwable> handler = obj -> { };

        QueryBuilder.queryFor("foobar", String.class)
            .withSink(capture::set)
            .waitingFor(123)
            .onError(handler)
            .orEmpty();

        assertThat(capture.get().getOnError()).isEqualTo(handler);
    }


    @Test
    void ensureBuilderCapturesThrowable() throws Exception {

        AtomicReference<QueryBuilder<String>> capture = new AtomicReference<>(null);

        Supplier<Throwable> throwable = RuntimeException::new;

        QueryBuilder.queryFor("foobar", String.class)
            .withSink(capture::set)
            .waitingFor(123)
            .orThrow(throwable);

        assertThat(capture.get().getOrThrows()).isEqualTo(throwable);
    }


    @Test
    void ensureCapturesDefaults() throws Exception {

        AtomicReference<QueryBuilder<String>> capture = new AtomicReference<>(null);

        Collection<String> defaults = List.of("foo", "bar", "baz");

        QueryBuilder.queryFor("foobar", String.class)
            .withSink(capture::set)
            .waitingFor(123)
            .orDefaults(defaults);

        assertThat(capture.get().getOrDefaults()).isNotNull();
        assertThat(capture.get().getOrDefaults().get()).isEqualTo(defaults);
    }


    @Test
    void ensureCapturesDefaultsSupplier() throws Exception {

        AtomicReference<QueryBuilder<String>> capture = new AtomicReference<>(null);

        Collection<String> defaults = List.of("foo", "bar", "baz");

        QueryBuilder.queryFor("foobar", String.class)
            .withSink(capture::set)
            .waitingFor(123)
            .orDefaults(() -> defaults);

        assertThat(capture.get().getOrDefaults()).isNotNull();
        assertThat(capture.get().getOrDefaults().get()).isEqualTo(defaults);
    }


    @Test
    void ensureThrowsForMoreTakingAtLeastThanAtMost() {

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> QueryBuilder.queryFor("foobar", String.class).waitingFor(123).takingAtMost(1).takingAtLeast(2));
    }


    @Test
    void ensureThrowsForEqualTakingAtLeastAsAtMost() {

        Assertions.assertThrows(IllegalArgumentException.class,
            () -> QueryBuilder.queryFor("foobar", String.class).waitingFor(123).takingAtMost(1).takingAtLeast(1));
    }
}
