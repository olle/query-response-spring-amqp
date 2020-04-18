package com.studiomediatech.queryresponse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class ResponseBuilderTest {

    @Mock
    ResponseRegistry registry;

    @Captor
    ArgumentCaptor<ResponseBuilder<String>> responses;

    @Test
    @DisplayName("can capture builder withSink(..) and assert it's state")
    void ensureWithSinkCapturesBuilderForTesting() throws Exception {

        AtomicReference<ResponseBuilder<String>> capture = new AtomicReference<>(null);

        ResponseBuilder.<String>respondTo("foobar", String.class)
            .withSink(capture::set)
            .withAll()
            .from("hello", "world!");

        ResponseBuilder<String> builder = capture.get();
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
    @DisplayName("builder supplies iterator and correct count for collection")
    void ensureBuilderSuppliesCollectionIteratorAndTotalCount() throws Exception {

        var capture = new AtomicReference<ResponseBuilder<String>>(null);

        List<String> elements = List.of("foo", "bar", "baz");

        ResponseBuilder.<String>respondTo("some-query", String.class)
            .withSink(capture::set)
            .withAll()
            .from(elements);

        ResponseBuilder<String> builder = capture.get();
        assertThat(builder).isNotNull();

        assertThat(builder.total()).isNotNull();
        assertThat(builder.total().get()).isEqualTo(3);
        assertThat(builder.elements()).isNotNull();
        assertThat(asList(builder.elements().get())).containsExactly("foo", "bar", "baz");
    }


    @Test
    @DisplayName("builder supplies iterator and correct count for varargs")
    void ensureBuilderSuppliesVarargsIteratorAndTotalCount() throws Exception {

        var capture = new AtomicReference<ResponseBuilder<String>>(null);

        ResponseBuilder.<String>respondTo("some-query", String.class)
            .withSink(capture::set)
            .withAll()
            .from("foo", "bar", "baz");

        ResponseBuilder<String> builder = capture.get();
        assertThat(builder).isNotNull();

        assertThat(builder.total().get()).isEqualTo(3);
        assertThat(builder.elements()).isNotNull();
        assertThat(asList(builder.elements().get())).containsExactly("foo", "bar", "baz");
    }


    @Test
    @DisplayName("builder supplied iterator and total for suppliers")
    void ensureBuilderSuppliesSuppliedIteratorAndTotalCount() throws Exception {

        var capture = new AtomicReference<ResponseBuilder<String>>(null);

        Supplier<Iterator<String>> elements = List.of("foo", "bar", "baz")::iterator;
        Supplier<Integer> total = () -> 42;

        ResponseBuilder.<String>respondTo("some-query", String.class)
            .withSink(capture::set)
            .withAll()
            .from(elements, total);

        ResponseBuilder<String> builder = capture.get();
        assertThat(builder).isNotNull();

        assertThat(builder.total().get()).isEqualTo(42);
        assertThat(builder.elements()).isNotNull();
        assertThat(asList(builder.elements().get())).containsExactly("foo", "bar", "baz");
    }


    @Test
    void ensureBuilderIsCollectionForSingleScalarVararg() throws Exception {

        var builder = new AtomicReference<ResponseBuilder<String>>(null);

        ResponseBuilder.<String>respondTo("some-query", String.class)
            .withSink(builder::set)
            .withAll()
            .from("foobar");

        ResponseBuilder<String> b = builder.get();
        assertThat(b).isNotNull();

        assertThat(b.getRespondToTerm()).isEqualTo("some-query");
        assertThat(b.getBatchSize()).isEqualTo(0);
        assertThat(b.total().get()).isEqualTo(1);
        assertThat(b.elements()).isNotNull();
        assertThat(asList(b.elements().get())).containsExactly("foobar");
    }


    @Test
    void ensureBuilderIsCollectionForCoercedCollection() throws Exception {

        var builder = new AtomicReference<ResponseBuilder<?>>(null);

        var list = List.of("foo", "bar", "baz");

        ResponseBuilder.respondTo("some-query", String.class)
            .withSink(builder::set)
            .withAll()
            .from(list);

        @SuppressWarnings("unchecked")
        ResponseBuilder<String> b = (ResponseBuilder<String>) builder.get();
        assertThat(b).isNotNull();

        assertThat(b.getRespondToTerm()).isEqualTo("some-query");
        assertThat(b.getBatchSize()).isEqualTo(0);
        assertThat(b.total().get()).isEqualTo(3);
        assertThat(b.elements()).isNotNull();
        assertThat(asList(b.elements().get())).containsExactly("foo", "bar", "baz");
    }


    @Test
    void ensureBuilderHasSetBatchSize() throws Exception {

        var builder = new AtomicReference<ResponseBuilder<?>>(null);

        ResponseBuilder.respondTo("some-query", String.class)
            .withSink(builder::set)
            .withBatchesOf(3)
            .from("foo", "bar", "baz");

        ResponseBuilder<?> b = builder.get();
        assertThat(b).isNotNull();

        assertThat(b.getRespondToTerm()).isEqualTo("some-query");
        assertThat(b.getBatchSize()).isEqualTo(3);
    }


    @SuppressWarnings("static-access")
    @Test
    void ensureBuilderWillInvokeTheRegistryOnTerminalCall() throws Exception {

        try {
            ResponseRegistry.instance = () -> registry;

            ResponseBuilder.respondTo("foobar", String.class)
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
