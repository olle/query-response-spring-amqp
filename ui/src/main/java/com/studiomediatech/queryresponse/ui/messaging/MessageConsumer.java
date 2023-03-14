package com.studiomediatech.queryresponse.ui.messaging;

import java.io.IOException;
import java.util.Optional;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studiomediatech.queryresponse.stats.Stats;
import com.studiomediatech.queryresponse.ui.ConfigureMessaging;
import com.studiomediatech.queryresponse.ui.service.StatsHandlerAdapter;
import com.studiomediatech.queryresponse.util.Logging;

/**
 * Consumes Query/Response messages from the internal topics, and directly delegates for handling via the abstract
 * adapter.
 */
@Component
class MessageConsumer implements Logging {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * {@link AcknowledgeMode#NONE}
     */
    private static final String ACK_MODE = "NONE";
    private static final String CONSUMERS_MIN = "3";
    private static final String CONSUMERS_MAX = "11";

    private final StatsHandlerAdapter adapter;

    public MessageConsumer(Optional<StatsHandlerAdapter> maybe) {
        this.adapter = maybe.orElse(StatsHandlerAdapter.empty());
    }

    @RabbitListener(//
            queues = "#{@" + ConfigureMessaging.QUERY_RESPONSE_STATS_QUEUE_BEAN + "}", //
            ackMode = ACK_MODE, concurrency = CONSUMERS_MIN + "-" + CONSUMERS_MAX)
    void onQueryResponseStats(Message message) {
        try {
            adapter.handleConsumed(MAPPER.readValue(message.getBody(), Stats.class));
        } catch (RuntimeException | IOException ex) {
            log().error("Failed to consumed stats", ex);
        }
    }
}
