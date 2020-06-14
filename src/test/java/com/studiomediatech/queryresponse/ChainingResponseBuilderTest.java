package com.studiomediatech.queryresponse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class ChainingResponseBuilderTest {

    @Mock
    ResponseRegistry registry;

    @Captor
    ArgumentCaptor<ChainingResponseBuilder<String>> responses;

    @Test
    @DisplayName("can capture builder withSink(..) and assert it's state")
    void ensureWithSinkCapturesBuilderForTesting() throws Exception {

        AtomicReference<ChainingResponseBuilder<String>> capture = new AtomicReference<>(null);

        ChainingResponseBuilder.<String>respondTo("foobar", String.class).withSink(capture::set).withAll()
            .from("hello", "world!");

        ChainingResponseBuilder<String> builder = capture.get();
        assertThat(builder).isNotNull();

        assertThat(builder.getRespondToTerm()).isEqualTo("foobar");
        assertThat(asList(builder.elements().get())).containsExactly("hello", "world!");
    }


    private List<String> asList(Iterator<String> iterator) {

        List<String> list = new ArrayList<>();
        iterator.forEachRemaining(list::add);

        return list;
    }


    @Test
    @DisplayName("builder has iterator for provided collection")
    void ensureBuilderHasCollectionIterator() throws Exception {

        var capture = new AtomicReference<ChainingResponseBuilder<String>>(null);

        List<String> elements = List.of("foo", "bar", "baz");

        ChainingResponseBuilder.<String>respondTo("some-query", String.class)
            .withSink(capture::set)
            .withAll()
            .from(elements);

        ChainingResponseBuilder<String> builder = capture.get();
        assertThat(builder).isNotNull();

        assertThat(builder.elements()).isNotNull();
        assertThat(asList(builder.elements().get())).containsExactly("foo", "bar", "baz");
    }


    @Test
    @DisplayName("builder has iterator for varargs")
    void ensureBuilderHasVarargsIterator() throws Exception {

        var capture = new AtomicReference<ChainingResponseBuilder<String>>(null);

        ChainingResponseBuilder.<String>respondTo("some-query", String.class)
            .withSink(capture::set)
            .withAll()
            .from("foo", "bar", "baz");

        ChainingResponseBuilder<String> builder = capture.get();
        assertThat(builder).isNotNull();

        assertThat(builder.elements()).isNotNull();
        assertThat(asList(builder.elements().get())).containsExactly("foo", "bar", "baz");
    }


    @Test
    @DisplayName("builder has supplied iterator")
    void ensureBuilderHasSuppliedIterator() throws Exception {

        var capture = new AtomicReference<ChainingResponseBuilder<String>>(null);

        Supplier<Iterator<String>> elements = List.of("foo", "bar", "baz")::iterator;

        ChainingResponseBuilder.<String>respondTo("some-query", String.class)
            .withSink(capture::set)
            .withAll()
            .from(elements);

        ChainingResponseBuilder<String> builder = capture.get();
        assertThat(builder).isNotNull();

        assertThat(builder.elements()).isNotNull();
        assertThat(asList(builder.elements().get())).containsExactly("foo", "bar", "baz");
    }


    @Test
    @DisplayName("builder has supplied collection elements")
    void ensureSuppliedByCollection() throws Exception {

        var capture = new AtomicReference<ChainingResponseBuilder<String>>(null);

        Supplier<Collection<String>> elements = () -> List.of("foo", "bar", "baz");

        ChainingResponseBuilder.<String>respondTo("some-query", String.class)
            .withSink(capture::set)
            .withAll()
            .suppliedBy(elements);

        ChainingResponseBuilder<String> builder = capture.get();
        assertThat(builder).isNotNull();

        assertThat(builder.elements()).isNotNull();
        assertThat(asList(builder.elements().get())).containsExactly("foo", "bar", "baz");
    }


    @Test
    void ensureBuilderIsCollectionForSingleScalarVararg() throws Exception {

        var builder = new AtomicReference<ChainingResponseBuilder<String>>(null);

        ChainingResponseBuilder.<String>respondTo("some-query", String.class)
            .withSink(builder::set)
            .withAll()
            .from("foobar");

        ChainingResponseBuilder<String> b = builder.get();
        assertThat(b).isNotNull();

        assertThat(b.getRespondToTerm()).isEqualTo("some-query");
        assertThat(b.getBatchSize()).isEqualTo(0);
        assertThat(b.elements()).isNotNull();
        assertThat(asList(b.elements().get())).containsExactly("foobar");
    }


    @Test
    void ensureBuilderIsCollectionForCoercedCollection() throws Exception {

        var builder = new AtomicReference<ChainingResponseBuilder<?>>(null);

        var list = List.of("foo", "bar", "baz");

        ChainingResponseBuilder.respondTo("some-query", String.class)
            .withSink(builder::set)
            .withAll()
            .from(list);

        @SuppressWarnings("unchecked")
        ChainingResponseBuilder<String> b = (ChainingResponseBuilder<String>) builder.get();
        assertThat(b).isNotNull();

        assertThat(b.getRespondToTerm()).isEqualTo("some-query");
        assertThat(b.getBatchSize()).isEqualTo(0);
        assertThat(b.elements()).isNotNull();
        assertThat(asList(b.elements().get())).containsExactly("foo", "bar", "baz");
    }


    @Test
    void ensureBuilderHasSetBatchSize() throws Exception {

        var builder = new AtomicReference<ChainingResponseBuilder<?>>(null);

        ChainingResponseBuilder.respondTo("some-query", String.class)
            .withSink(builder::set)
            .withBatchesOf(3)
            .from("foo", "bar", "baz");

        ChainingResponseBuilder<?> b = builder.get();
        assertThat(b).isNotNull();

        assertThat(b.getRespondToTerm()).isEqualTo("some-query");
        assertThat(b.getBatchSize()).isEqualTo(3);
    }


    @SuppressWarnings("static-access")
    @Test
    void ensureBuilderWillInvokeTheRegistryOnTerminalCall() throws Exception {

        try {
            ResponseRegistry.instance = () -> registry;

            ChainingResponseBuilder.respondTo("foobar", String.class)
                .withAll()
                .from("foo", "bar", "baz");

            verify(registry).register(responses.capture());

            var builder = responses.getValue();

            assertThat(builder).isNotNull();
            assertThat(builder.getBatchSize()).isEqualTo(0);
            assertThat(builder.getRespondToTerm()).isEqualTo("foobar");
            assertThat(asList(builder.elements().get())).containsExactlyInAnyOrder("foo", "bar", "baz");
        } finally {
            ResponseRegistry.instance = () -> null;
        }
    }
}
