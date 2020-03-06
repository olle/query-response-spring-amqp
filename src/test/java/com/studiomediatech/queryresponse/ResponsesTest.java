package com.studiomediatech.queryresponse;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;


class ResponsesTest {

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
}
