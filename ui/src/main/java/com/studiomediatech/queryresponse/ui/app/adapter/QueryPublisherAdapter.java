package com.studiomediatech.queryresponse.ui.app.adapter;

import java.util.Collections;
import java.util.Map;

public interface QueryPublisherAdapter {

    static QueryPublisherAdapter empty() {
        return new QueryPublisherAdapter() {
            // OK
        };
    }

    default Map<String, Object> query(String q, int timeout, int limit) {
        return Collections.emptyMap();
    }
}
