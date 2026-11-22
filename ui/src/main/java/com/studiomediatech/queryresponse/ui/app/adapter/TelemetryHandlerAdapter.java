package com.studiomediatech.queryresponse.ui.app.adapter;

import com.studiomediatech.queryresponse.ui.messaging.Stats;
import com.studiomediatech.queryresponse.util.Loggable;

/**
 * Declares the capabilities of the incoming side for statistics to aggregate.
 */
public interface TelemetryHandlerAdapter extends Loggable {

    static TelemetryHandlerAdapter empty() {
        return new TelemetryHandlerAdapter() {
            // OK
        };
    }

    default void handleConsumed(Stats stats) {
        logger().warn("NOT YET HANDLING {}", stats);
    }
}
