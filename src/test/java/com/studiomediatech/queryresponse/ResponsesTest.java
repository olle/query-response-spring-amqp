package com.studiomediatech.queryresponse;

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

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class ResponsesTest {

    @Mock
    ResponseRegistry registry;

    @Captor
    ArgumentCaptor<Responses<String>> responses;

    @BeforeEach
    void setup() {

        ResponseRegistry.instance = () -> registry;
    }


    @SuppressWarnings("static-access")
    @Test
    @DisplayName("responses with all sets batch size to 0")
    void ensureConfiguresBuilderCorrectlyForAll() throws Exception {

        Responses.respondTo("foobar").withAll().from("foo", "bar", "baz");
        verify(registry).register(responses.capture());

        assertThat(responses.getValue()).isNotNull();
        assertThat(responses.getValue().getBatchSize()).isEqualTo(0);
        assertThat(responses.getValue().getTerm()).isEqualTo("foobar");
        assertThat(responses.getValue().getElements()).containsExactlyInAnyOrder("foo", "bar", "baz");
    }


    @SuppressWarnings("static-access")
    @Test
    @DisplayName("responses with batch size is correctly set")
    void ensureConfiguresBuilderCorrectlyForBatches() throws Exception {

        Responses.respondTo("foobar").withBatchesOf(2).from("foo", "bar", "baz");
        verify(registry).register(responses.capture());

        assertThat(responses.getValue()).isNotNull();
        assertThat(responses.getValue().getBatchSize()).isEqualTo(2);
        assertThat(responses.getValue().getTerm()).isEqualTo("foobar");
        assertThat(responses.getValue().getElements()).containsExactlyInAnyOrder("foo", "bar", "baz");
    }


    @Test
    void ensureThrowsOnTooLongTerm() {

        assertThrows(IllegalArgumentException.class, () -> Responses.respondTo("a".repeat(256)));
    }


    @Test
    void ensureThrowsForNullQueryTerm() throws Exception {

        assertThrows(IllegalArgumentException.class, () -> Responses.respondTo(null));
    }


    @Test
    void ensureThrowsForEmptyQueryTerm() throws Exception {

        assertThrows(IllegalArgumentException.class, () -> Responses.respondTo(""));
    }


    @Test
    void ensureThrowsForWhitespaceQueryTerm() throws Exception {

        assertThrows(IllegalArgumentException.class, () -> Responses.respondTo("     "));
    }


    @Test
    void ensureThrowsForBatchSizeOfZero() throws Exception {

        assertThrows(IllegalArgumentException.class, () -> Responses.respondTo("foobar").withBatchesOf(0));
    }


    @Test
    void ensureThrowsForNegativeBatchSize() throws Exception {

        assertThrows(IllegalArgumentException.class, () -> Responses.respondTo("foobar").withBatchesOf(-23));
    }


    @Test
    void ensureThrowsForEmptyVarargsArray() throws Exception {

        assertThrows(IllegalArgumentException.class,
            () -> Responses.respondTo("foobar").withAll().from(new Object[] {}));
    }


    @Test
    void ensureThrowsForNullVarargsArray() throws Exception {

        assertThrows(IllegalArgumentException.class,
            () -> Responses.respondTo("foobar").withAll().from((Object[]) null));
    }


    @Test
    void ensureThrowsForNullElementsInVarargs() throws Exception {

        assertThrows(IllegalArgumentException.class,
            () -> Responses.respondTo("foobar").withAll().from("hello", null, "fake"));
    }


    @Test
    void ensureThrowsForNullResponseCollection() throws Exception {

        Collection<String> nope = (Collection<String>) null;
        assertThrows(IllegalArgumentException.class, () -> Responses.<String>respondTo("foobar").withAll().from(nope));
    }


    @Test
    void ensureThrowsForNullInResponseCollection() throws Exception {

        assertThrows(IllegalArgumentException.class,
            () -> Responses.respondTo("foobar").withAll().from(Arrays.asList("foo", null, "bar")));
    }
}
