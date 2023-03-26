package com.studiomediatech.queryresponse.ui.app.adapter;

public interface WebSocketApiAdapter {

    static WebSocketApiAdapter empty() {
        return new WebSocketApiAdapter() {
            // OK
        };
    }
}
