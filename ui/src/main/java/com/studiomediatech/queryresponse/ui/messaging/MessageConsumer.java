package com.studiomediatech.queryresponse.ui.messaging;

import java.io.IOException;
import java.util.Optional;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studiomediatech.Stats;
import com.studiomediatech.queryresponse.ui.ConfigureMessaging;
import com.studiomediatech.queryresponse.util.Logging;

@Component
public class MessageConsumer implements Logging {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final MessageConsumerAdatper adapter;

    public MessageConsumer(Optional<MessageConsumerAdatper> maybe) {
        this.adapter = maybe.orElse(MessageConsumerAdatper.empty());
    }

    @RabbitListener(queues = "#{@" + ConfigureMessaging.QUERY_RESPONSE_STATS_QUEUE_BEAN + "}")
    void onQueryResponseStats(Message message, @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String key) {

        if (ConfigureMessaging.QUERY_RESPONSE_INTERNAL_STATS_ROUTING_KEY.equals(key)) {
            try {
                adapter.handle(MAPPER.readValue(message.getBody(), Stats.class).elements());
            } catch (RuntimeException | IOException ex) {
                log().error("Failed to consumed stats", ex);
            }
        }
    }
}
