package com.studiomediatech.queryresponse.ui.messaging;

import java.util.Collection;

import com.studiomediatech.Stat;
import com.studiomediatech.queryresponse.util.Logging;

public interface MessageConsumerAdatper extends Logging {

    static MessageConsumerAdatper empty() {
        return new MessageConsumerAdatper() {
            // OK
        };
    }

    default void handle(Collection<Stat> elements) {
        log().warn("NOT YET HANDLING {}", elements);
    }

}
