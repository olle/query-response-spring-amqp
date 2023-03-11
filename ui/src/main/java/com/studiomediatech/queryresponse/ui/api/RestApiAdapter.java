package com.studiomediatech.queryresponse.ui.api;

import java.util.Collections;
import java.util.Map;

public interface RestApiAdapter {

    static RestApiAdapter empty() {
        return new RestApiAdapter() {
            // OK
        };
    }

    default Map<String, Object> query(String q) {
        return Collections.emptyMap();
    }
}
