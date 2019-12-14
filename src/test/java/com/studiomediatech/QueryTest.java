package com.studiomediatech;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class QueryTest {

    @Test
    void ensureQueryResolvesToEmptyResult() {

        var start = System.currentTimeMillis();
        Query.<String>queryFor("anything").waitingFor(200).orEmpty();

        var end = System.currentTimeMillis();
        assertThat(end - start).isGreaterThanOrEqualTo(200);
    }
}
