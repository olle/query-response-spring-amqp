package com.studiomediatech.queryresponse.ui.messaging;

import java.io.IOException;
import java.util.Optional;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studiomediatech.queryresponse.ui.app.adapter.TelemetryHandlerAdapter;
import com.studiomediatech.queryresponse.util.Loggable;

/**
 * Consumes Query/Response telemetry messages from the internal topics, and directly delegates for handling via the
 * abstract adapter.
 */
@Component
class TelemetryConsumerPort implements Loggable {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * {@link AcknowledgeMode#NONE}
     */
    private static final String NONE = "NONE";
    private static final String CONSUMERS_MIN = "1";
    private static final String CONSUMERS_MAX = "7";

    private final TelemetryHandlerAdapter adapter;

    public TelemetryConsumerPort(Optional<TelemetryHandlerAdapter> maybe) {
        this.adapter = maybe.orElse(TelemetryHandlerAdapter.empty());
    }

    @RabbitListener(//
            queues = "#{@" + MessagingConfig.QUERY_RESPONSE_STATS_QUEUE_BEAN + "}", //
            ackMode = NONE, concurrency = CONSUMERS_MIN + "-" + CONSUMERS_MAX)
    void onQueryResponseStats(Message message) {
        try {
            Stats stats = MAPPER.readValue(message.getBody(), Stats.class);
            adapter.handleConsumed(stats);
        } catch (RuntimeException | IOException ex) {
            logger().error("Failed to consumed telemetry message", ex);
        }
    }
}
