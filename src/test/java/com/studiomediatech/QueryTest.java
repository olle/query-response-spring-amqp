package com.studiomediatech;

import org.junit.jupiter.api.Test;


class QueryTest {

    @Test
    void ensureQueryResolvesToEmptyResult() {

        Response<String> names = Query.<String>queryFor("names").waitingFor(200).orEmpty();
    }
}
