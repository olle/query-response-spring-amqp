package com.studiomediatech.queryresponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.studiomediatech.queryresponse.util.Logging;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;


/**
 * Represents an active published query, as the registered message listener for any responses, handling aggregation,
 * fallback, failure delegation and throwing, all depending on the configuration of this query.
 *
 * @param  <T>  expected type of the coerced result elements.
 */
class Query<T> implements MessageListener, Logging {

    private static final ObjectReader reader = new ObjectMapper().reader();

    private final Queries<T> queries;
    private final AtomicReference<Collection<T>> results;

    public Query(Queries<T> queries) {

        this.queries = queries;
        this.results = new AtomicReference<>(Collections.emptyList());
    }

    @Override
    public void onMessage(Message message) {

        log().info("|--> Received response message: {}", message.getMessageProperties());
        handleResponseEnvelope(parseMessage(message));
    }


    private ConsumedResponseEnvelope<T> parseMessage(Message message) {

        try {
            return reader.forType(TypeFactory.defaultInstance()
                        .constructParametricType(ConsumedResponseEnvelope.class, queries.getType()))
                .readValue(message.getBody());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return ConsumedResponseEnvelope.empty();
    }


    void handleResponseEnvelope(ConsumedResponseEnvelope<T> envelope) {

        if (envelope.elements.isEmpty()) {
            log().warn("Received empty response", envelope);

            return;
        }

        results.set(envelope.elements);
    }


    public Collection<T> publish(RabbitTemplate rabbit, String queue) {

        publishQuery(rabbit, queue);

        try {
            Thread.sleep(queries.getWaitingFor().toMillis());
        } catch (InterruptedException e) {
            // TODO: Apply to provided onError-handler
            e.printStackTrace();
        }

        if (results.get().isEmpty()) {
            if (queries.getOrDefaults() != null) {
                return queries.getOrDefaults().get();
            }
            // TODO: Or throws, etc.
        }

        return results.get();
    }


    private void publishQuery(RabbitTemplate rabbit, String queue) {

        try {
            var message = MessageBuilder.withBody("{}".getBytes()).setReplyTo(queue).build();

            rabbit.send("queries", queries.getQueryForTerm(), message);
            log().info("|<-- Published query: {}", queries.getQueryForTerm());
        } catch (RuntimeException e) {
            // TODO: Apply to provided onError-handler
            e.printStackTrace();
        }
    }


    static <T> Query<T> build(Queries<T> queries) {

        // TODO Flesh-out this static factory method.
        return new Query<>(queries);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class ConsumedResponseEnvelope<R> {

        @JsonProperty
        public int count;
        @JsonProperty
        public int total;
        @JsonProperty
        public Collection<R> elements = new ArrayList<>();

        public static <R> ConsumedResponseEnvelope<R> empty() {

            return new ConsumedResponseEnvelope<>();
        }


        @Override
        public String toString() {

            return "ConsumedResponseEnvelope [count=" + count + ", total=" + total + "]";
        }
    }
}
