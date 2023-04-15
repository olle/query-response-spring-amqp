package com.studiomediatech.queryresponse.ui.app.adapter;

import com.studiomediatech.queryresponse.ui.messaging.Stats;
import com.studiomediatech.queryresponse.util.Logging;

/**
 * Declares the capabilities of the incoming side for statistics to aggregate.
 */
public interface TelemetryHandlerAdapter extends Logging {

    static TelemetryHandlerAdapter empty() {
        return new TelemetryHandlerAdapter() {
            // OK
        };
    }

    default void handleConsumed(Stats stats) {
        log().warn("NOT YET HANDLING {}", stats);
    }
}
