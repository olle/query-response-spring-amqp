package com.studiomediatech.queryresponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mockito;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
class ResponsesTest {

    @BeforeEach
    void setup() {

        RespondingRegistry.instance = () -> Mockito.mock(RespondingRegistry.class);
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

        assertThrows(IllegalArgumentException.class, () -> Responses.respondTo("foobar").withAll().from(new Object[] {}));
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
}
