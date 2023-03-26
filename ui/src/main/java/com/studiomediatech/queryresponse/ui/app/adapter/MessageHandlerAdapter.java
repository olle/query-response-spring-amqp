package com.studiomediatech.queryresponse.ui.app.adapter;

import com.studiomediatech.queryresponse.ui.messaging.Stats;
import com.studiomediatech.queryresponse.util.Logging;

/**
 * Declares the capabilities of the incoming side for statistics to aggregate.
 */
public interface MessageHandlerAdapter extends Logging {

    static MessageHandlerAdapter empty() {
        return new MessageHandlerAdapter() {
            // OK
        };
    }

    default void handleConsumed(Stats stats) {
        log().warn("NOT YET HANDLING {}", stats);
    }
}
