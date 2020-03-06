package com.studiomediatech.queryresponse;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertThrows;


class QueriesTest {

    @Test
    void ensureThrowsOnTooLongQueryTerm() {

        assertThrows(IllegalArgumentException.class, () -> Queries.queryFor("a".repeat(256), String.class));
    }


    @Test
    void ensureThrowsForNullQueryTerm() throws Exception {

        assertThrows(IllegalArgumentException.class, () -> Queries.queryFor(null, String.class));
    }


    @Test
    void ensureThrowsForEmptyQueryTerm() throws Exception {

        assertThrows(IllegalArgumentException.class, () -> Queries.queryFor("", String.class));
    }


    @Test
    void ensureThrowsForWhitespaceQueryTerm() throws Exception {

        assertThrows(IllegalArgumentException.class, () -> Queries.queryFor("     ", String.class));
    }


    @Test
    void ensureThrowsForZeroDuration() throws Exception {

        assertThrows(IllegalArgumentException.class, () -> Queries.queryFor("foobar", String.class).waitingFor(0L));
    }


    @Test
    void ensureThrowsForNegativeDuration() throws Exception {

        assertThrows(IllegalArgumentException.class, () -> Queries.queryFor("foobar", String.class).waitingFor(-2L));
    }


    @Test
    void ensureThrowsForExcessiveLongDuration() throws Exception {

        Duration tooLong = Duration.ofMillis(Long.MAX_VALUE).plusMillis(1);
        assertThrows(IllegalArgumentException.class,
            () -> Queries.queryFor("foobar", String.class).waitingFor(tooLong));
    }


    @Test
    void ensureThrowsForNegativeTakingAtMost() throws Exception {

        assertThrows(IllegalArgumentException.class,
            () -> Queries.queryFor("foobar", String.class).waitingFor(123).takingAtMost(-1));
    }


    @Test
    void ensureThrowsForZeroTakingAtMost() throws Exception {

        assertThrows(IllegalArgumentException.class,
            () -> Queries.queryFor("foobar", String.class).waitingFor(123).takingAtMost(0));
    }


    @Test
    void ensureThrowsForNegativeTakingAtLeast() throws Exception {

        assertThrows(IllegalArgumentException.class,
            () -> Queries.queryFor("foobar", String.class).waitingFor(123).takingAtLeast(-1));
    }


    @Test
    void ensureThrowsForZeroTakingAtLeast() throws Exception {

        assertThrows(IllegalArgumentException.class,
            () -> Queries.queryFor("foobar", String.class).waitingFor(123).takingAtLeast(0));
    }


    @Test
    void ensureThrowsForMoreTakingAtLeastThanAtMost() throws Exception {

        assertThrows(IllegalArgumentException.class,
            () -> Queries.queryFor("foobar", String.class).waitingFor(123).takingAtMost(1).takingAtLeast(2));
    }


    @Test
    void ensureThrowsForEqualTAkingAtLeastAsAtMost() throws Exception {

        assertThrows(IllegalArgumentException.class,
            () -> Queries.queryFor("foobar", String.class).waitingFor(123).takingAtMost(1).takingAtLeast(1));
    }
}
