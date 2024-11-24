package com.studiomediatech.queryresponse.ui.app.adapter;

import java.util.Collection;

import com.studiomediatech.queryresponse.ui.app.telemetry.Node;
import com.studiomediatech.queryresponse.util.Logging;

public interface WebSocketApiAdapter extends Logging {

    static WebSocketApiAdapter empty() {
        return new WebSocketApiAdapter() {
            // OK
        };
    }

    default void publishNodes(Collection<Node> nodes) {
        log().warn("NOT PUBLISHING NODES! {}", nodes);
    }
}
