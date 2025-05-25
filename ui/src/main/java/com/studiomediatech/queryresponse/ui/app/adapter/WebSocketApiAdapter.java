package com.studiomediatech.queryresponse.ui.app.adapter;

import java.util.Collection;

import com.studiomediatech.queryresponse.ui.app.telemetry.Node;
import com.studiomediatech.queryresponse.util.Loggable;

public interface WebSocketApiAdapter extends Loggable {

    static WebSocketApiAdapter empty() {
        return new WebSocketApiAdapter() {
            // OK
        };
    }

    default void publishNodes(Collection<Node> nodes) {
        logger().warn("NOT PUBLISHING NODES! {}", nodes);
    }
}
