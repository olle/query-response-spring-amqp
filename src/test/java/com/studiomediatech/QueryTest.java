package com.studiomediatech;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


class QueryTest {

    @Test
    void ensureQueryResolvesToEmptyResult() {

        var start = System.currentTimeMillis();

        List<String> things = Query.<String>queryFor("anything").waitingFor(200).orEmpty()
                .collect(Collectors.toList());

        var end = System.currentTimeMillis();

        assertThat(end - start).isGreaterThanOrEqualTo(200);
        assertThat(things).isEmpty();
    }
}
