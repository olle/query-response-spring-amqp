package com.studiomediatech.queryresponse.ui.app.adapter;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

public interface RestApiAdapter {

    static RestApiAdapter empty() {
        return new RestApiAdapter() {
            // OK
        };
    }

    default Map<String, Object> query(String q, int timeout, int limit) {
        return Collections.emptyMap();
    }

    default Map<String, Object> nodes() {
        return Map.of("elements", Collections.emptyList());
    }

    default Map<String, Object> nodesV1() {
        return Map.of("nodes", Collections.emptyMap(), "timestamp", Instant.now());
    }
}
