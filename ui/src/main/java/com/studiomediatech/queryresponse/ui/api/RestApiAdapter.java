package com.studiomediatech.queryresponse.ui.api;

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
        return Collections.emptyMap();
    }
}
