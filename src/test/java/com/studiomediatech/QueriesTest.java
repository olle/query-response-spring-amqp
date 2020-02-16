package com.studiomediatech;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;


class QueriesTest {

    @Test
    void ensureThrowsOnTooLongQueryTerm() {

        assertThrows(IllegalArgumentException.class, () -> Queries.queryFor("a".repeat(256)));
    }


    @Test
    void ensureThrowsForNullQueryTerm() throws Exception {

        assertThrows(IllegalArgumentException.class, () -> Queries.queryFor(null));
    }


    @Test
    void ensureThrowsForEmptyQueryTerm() throws Exception {

        assertThrows(IllegalArgumentException.class, () -> Queries.queryFor(""));
    }


    @Test
    void ensureThrowsForWhitespaceQueryTerm() throws Exception {

        assertThrows(IllegalArgumentException.class, () -> Queries.queryFor("     "));
    }


    @Test
    void ensureThrowsForZeroDuration() throws Exception {

        assertThrows(IllegalArgumentException.class, () -> Queries.queryFor("foobar").waitingFor(0L));
    }


    @Test
    void ensureThrowsForNegativeDuration() throws Exception {

        assertThrows(IllegalArgumentException.class, () -> Queries.queryFor("foobar").waitingFor(-2L));
    }
}
