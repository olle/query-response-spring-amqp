package com.studiomediatech.queryresponse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class ResponseBuilderTest {

    @Mock
    ResponseRegistry registry;

    @Captor
    ArgumentCaptor<ResponseBuilder<String>> responses;

    @BeforeEach
    void setup() {

        ResponseRegistry.instance = () -> registry;
    }


    @AfterEach
    void teardown() {

        ResponseRegistry.instance = () -> null;
    }


    @SuppressWarnings("static-access")
    @Test
    @DisplayName("responses with all sets batch size to 0")
    void ensureConfiguresBuilderCorrectlyForAll() {

        ResponseBuilder.respondTo("foobar").withAll().from("foo", "bar", "baz");
        verify(registry).register(responses.capture());

        assertThat(responses.getValue()).isNotNull();
        assertThat(responses.getValue().getBatchSize()).isEqualTo(0);
        assertThat(responses.getValue().getTerm()).isEqualTo("foobar");
        assertThat(responses.getValue().getElementsCollection()).containsExactlyInAnyOrder("foo", "bar", "baz");
    }


    @SuppressWarnings("static-access")
    @Test
    @DisplayName("responses with batch size is correctly set")
    void ensureConfiguresBuilderCorrectlyForBatches() {

        ResponseBuilder.respondTo("foobar").withBatchesOf(2).from("foo", "bar", "baz");
        verify(registry).register(responses.capture());

        assertThat(responses.getValue()).isNotNull();
        assertThat(responses.getValue().getBatchSize()).isEqualTo(2);
        assertThat(responses.getValue().getTerm()).isEqualTo("foobar");
        assertThat(responses.getValue().getElementsCollection()).containsExactlyInAnyOrder("foo", "bar", "baz");
    }


    @Test
    void ensureThrowsForNullResponseCollection() throws Exception {

        Collection<String> nope = null;
        assertThrows(IllegalArgumentException.class,
            () -> ResponseBuilder.<String>respondTo("foobar").withAll().from(nope));
    }


    @Test
    void ensureThrowsForNullInResponseCollection() {

        assertThrows(IllegalArgumentException.class,
            () -> ResponseBuilder.respondTo("foobar").withAll().from(Arrays.asList("foo", null, "bar")));
    }


    @SuppressWarnings("static-access")
    @Test
    void ensureBuilderHoldsCollection() throws Exception {

        ResponseBuilder.respondTo("foobar").withAll().from(List.of("foo", "bar"));

        verify(registry).register(responses.capture());

        ResponseBuilder<String> r = responses.getValue();
        assertThat(r).isNotNull();

        assertThat(r.getElementsCollection()).containsExactly("foo", "bar");
        assertThat(r.getElementsIterator()).isNull();
        assertThat(r.getTotalSupplier()).isNotNull();
        assertThat(r.getTotalSupplier().get()).isEqualTo(2);
    }


    @SuppressWarnings("static-access")
    @Test
    @DisplayName("responses with iterator uses total supplier")
    void ensureBuilderHoldsIterator() throws Exception {

        Iterator<String> it = List.of("foo", "bar", "baz").iterator();
        ResponseBuilder.<String>respondTo("foobar").withBatchesOf(2).from(it, () -> 3);

        verify(registry).register(responses.capture());

        ResponseBuilder<String> r = responses.getValue();
        assertThat(r).isNotNull();

        assertThat(r.getElementsCollection()).isNull();
        assertThat(r.getElementsIterator()).isEqualTo(it);
        assertThat(r.getTotalSupplier()).isNotNull();
        assertThat(r.getTotalSupplier().get()).isEqualTo(3);
    }
}
