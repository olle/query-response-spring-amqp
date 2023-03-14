package com.studiomediatech.queryresponse.ui.service;

import com.studiomediatech.queryresponse.stats.Stats;
import com.studiomediatech.queryresponse.util.Logging;

/**
 * Declares the capabilities of the incoming side for statistics to aggregate.
 */
public interface StatsHandlerAdapter extends Logging {

    static StatsHandlerAdapter empty() {
        return new StatsHandlerAdapter() {
            // OK
        };
    }

    default void handleConsumed(Stats stats) {
        log().warn("NOT YET HANDLING {}", stats);
    }
}
