package com.studiomediatech.queryresponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class ResponseBuilderTest {

    @Mock
    ResponseRegistry registry;

    @Captor
    ArgumentCaptor<ResponseBuilder<String>> responses;

    @Test
    void ensureWithSinkCapturesBuilder() throws Exception {

        AtomicReference<ResponseBuilder<String>> capture = new AtomicReference<>(null);

        ResponseBuilder.<String>respondTo("foobar")
            .withSink(capture::set)
            .withAll()
            .from("hello", "world!");

        assertThat(capture.get()).isNotNull();
    }


    @Test
    void ensureBuilderIsCollectionForCollection() throws Exception {

        var builder = new AtomicReference<ResponseBuilder<?>>(null);

        ResponseBuilder.respondTo("some-query")
            .withSink(builder::set)
            .withAll()
            .from(List.of("foo", "bar", "baz"));

        ResponseBuilder<?> b = builder.get();
        assertThat(b).isNotNull();

        assertThat(b.getRespondToTerm()).isEqualTo("some-query");
        assertThat(b.getBatchSize()).isEqualTo(0);
        assertThat(b.getTotalSupplier().get()).isEqualTo(3);
        assertThat(b.getType()).isEqualTo(ResponseBuilder.Type.DIRECT);
    }


    @Test
    void ensureBuilderIsCollectionForVarargs() throws Exception {

        var builder = new AtomicReference<ResponseBuilder<?>>(null);

        ResponseBuilder.respondTo("some-query")
            .withSink(builder::set)
            .withAll()
            .from("foo", "bar", "baz");

        ResponseBuilder<?> b = builder.get();
        assertThat(b).isNotNull();

        assertThat(b.getRespondToTerm()).isEqualTo("some-query");
        assertThat(b.getBatchSize()).isEqualTo(0);
        assertThat(b.getTotalSupplier().get()).isEqualTo(3);
        assertThat(b.getType()).isEqualTo(ResponseBuilder.Type.DIRECT);
    }


    @Test
    void ensureBuilderIsCollectionForSingleScalarVararg() throws Exception {

        var builder = new AtomicReference<ResponseBuilder<?>>(null);

        ResponseBuilder.respondTo("some-query")
            .withSink(builder::set)
            .withAll()
            .from("foo");

        ResponseBuilder<?> b = builder.get();
        assertThat(b).isNotNull();

        assertThat(b.getRespondToTerm()).isEqualTo("some-query");
        assertThat(b.getBatchSize()).isEqualTo(0);
        assertThat(b.getTotalSupplier().get()).isEqualTo(1);
        assertThat(b.getType()).isEqualTo(ResponseBuilder.Type.DIRECT);
    }


    @Test
    void ensureBuilderIsCollectionForCoercedCollection() throws Exception {

        var builder = new AtomicReference<ResponseBuilder<?>>(null);

        var list = List.of("foo", "bar", "baz");

        ResponseBuilder.respondTo("some-query")
            .withSink(builder::set)
            .withAll()
            .from(list);

        ResponseBuilder<?> b = builder.get();
        assertThat(b).isNotNull();

        assertThat(b.getRespondToTerm()).isEqualTo("some-query");
        assertThat(b.getBatchSize()).isEqualTo(0);
        assertThat(b.getTotalSupplier().get()).isEqualTo(3);
        assertThat(b.getType()).isEqualTo(ResponseBuilder.Type.DIRECT);
    }


    @Test
    void ensureBuilderHasSetBatchSize() throws Exception {

        var builder = new AtomicReference<ResponseBuilder<?>>(null);

        ResponseBuilder.respondTo("some-query")
            .withSink(builder::set)
            .withBatchesOf(3)
            .from("foo", "bar", "baz");

        ResponseBuilder<?> b = builder.get();
        assertThat(b).isNotNull();

        assertThat(b.getRespondToTerm()).isEqualTo("some-query");
        assertThat(b.getBatchSize()).isEqualTo(3);
        assertThat(b.getTotalSupplier().get()).isEqualTo(3);
        assertThat(b.getType()).isEqualTo(ResponseBuilder.Type.DIRECT);
    }


    @SuppressWarnings("static-access")
    @Test
    void ensureBuilderWillInvokeTheRegistryOnTerminalCall() throws Exception {

        try {
            ResponseRegistry.instance = () -> registry;

            ResponseBuilder.respondTo("foobar")
                .withAll()
                .from("foo", "bar", "baz");

            verify(registry).register(responses.capture());

            var builder = responses.getValue();

            assertThat(builder).isNotNull();
            assertThat(builder.getBatchSize()).isEqualTo(0);
            assertThat(builder.getRespondToTerm()).isEqualTo("foobar");
            assertThat(builder.getElementsCollection()).containsExactlyInAnyOrder("foo", "bar", "baz");
        } finally {
            ResponseRegistry.instance = () -> null;
        }
    }
}
