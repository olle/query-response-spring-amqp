package com.studiomediatech.queryresponse.ui.app.telemetry;

import java.util.Map;

import com.studiomediatech.queryresponse.ui.app.adapter.MessageHandlerAdapter;
import com.studiomediatech.queryresponse.ui.app.adapter.RestApiAdapter;
import com.studiomediatech.queryresponse.ui.app.adapter.WebSocketApiAdapter;
import com.studiomediatech.queryresponse.ui.messaging.Stat;
import com.studiomediatech.queryresponse.ui.messaging.Stats;
import com.studiomediatech.queryresponse.util.Logging;

public class TelemetryService implements Logging, MessageHandlerAdapter, RestApiAdapter {

    private final WebSocketApiAdapter webSocketApiAdapter;

    public TelemetryService(WebSocketApiAdapter webSocketApiAdapter) {
        this.webSocketApiAdapter = webSocketApiAdapter;
    }

    @Override
    public Map<String, Object> nodes() {
        return Map.of("nodes", "none");
    }

    @Override
    public void handleConsumed(Stats stats) {

        log().info("Consumed stats with {} elements", stats.elements().size());

        stats.elements().stream().filter(s -> s.timestamp() != null).map(Stat::toString)
                .forEach(str -> log().debug("STAT VALUE: {}", str));

        stats.elements().stream().filter(s -> s.timestamp() == null).map(Stat::toString)
                .forEach(str -> log().debug("STAT INFO: {}", str));

    }
}
