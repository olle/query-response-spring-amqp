package com.studiomediatech.queryresponse;

import com.studiomediatech.queryresponse.util.Logging;


class Statistics implements Logging {

    // OK

    public Statistics() {

        ResponseBuilder.respondTo("query-response/stats")
            .withAll()
            .from("hello", "world!");
    }
}
