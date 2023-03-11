package com.studiomediatech.queryresponse.ui.api;

public interface RestApiAdapter {

    static RestApiAdapter empty() {
        return new RestApiAdapter() {
            // OK
        };
    }
}
