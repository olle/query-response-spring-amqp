package com.studiomediatech.queryresponse;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;


class QueryBuilderTest {

    @Test
    void ensureThrowsForMoreTakingAtLeastThanAtMost() {

        assertThrows(IllegalArgumentException.class,
            () -> QueryBuilder.queryFor("foobar", String.class).waitingFor(123).takingAtMost(1).takingAtLeast(2));
    }


    @Test
    void ensureThrowsForEqualTakingAtLeastAsAtMost() {

        assertThrows(IllegalArgumentException.class,
            () -> QueryBuilder.queryFor("foobar", String.class).waitingFor(123).takingAtMost(1).takingAtLeast(1));
    }
}
