package com.studiomediatech.queryresponse.ui.app.adapter;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.studiomediatech.queryresponse.ui.messaging.Stat;
import com.studiomediatech.queryresponse.util.Loggable;

public interface WebSocketApiAdapter extends Loggable {

    static WebSocketApiAdapter empty() {
        return new WebSocketApiAdapter() {
            // OK
        };
    }

    default void publishNodes(Map<String, List<Stat>> nodes) {
        logger().warn("NOT PUBLISHING NODES! {}", nodes);
    }

    default void publishMetrics(Map<String, Object> metrics) {
        logger().warn("NOT PUBLISHING METRICS! {}", metrics);
    }

    default void publishResponse(Collection<Object> response, String id) {
        logger().warn("NOT PUBLISHING RESPONSE! {} to {}", response, id);
    }
}
