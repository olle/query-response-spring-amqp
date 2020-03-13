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
import org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer;

import java.io.IOException;

import java.time.Duration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 * Represents an active published query, as the registered message listener for any responses, handling aggregation,
 * fallback, failure delegation and throwing, all depending on the configuration of this query.
 *
 * @param  <T>  expected type of the coerced result elements.
 */
class Query<T> implements MessageListener, Logging {

    private static final ObjectReader reader = new ObjectMapper().reader();

    // TODO: Concurrency and aggregation, perhaps rather use a queue.
    private final AtomicReference<Collection<T>> results;

    protected String queryTerm;
    protected Class<T> responseType;
    protected Duration waitingFor;
    protected Supplier<Collection<T>> orDefaults;
    protected Consumer<Throwable> onError;

    // Declared protected, for access in unit tests.
    protected Query() {

        this.results = new AtomicReference<>(Collections.emptyList());
    }

    @Override
    public void onMessage(Message message) {

        log().info("|--> Received response message: {}", message.getMessageProperties());
        handleResponseEnvelope(parseMessage(message));
    }


    private ConsumedResponseEnvelope<T> parseMessage(Message message) {

        try {
            ConsumedResponseEnvelope<T> response = reader.forType(TypeFactory.defaultInstance()
                        .constructParametricType(ConsumedResponseEnvelope.class, responseType))
                    .readValue(message.getBody());
            log().debug("Received response: {}", response);

            return response;
        } catch (IOException ex) {
            if (onError != null) {
                onError.accept(new IllegalArgumentException(
                        "Failed to parse response to elements of type " + responseType.getSimpleName(), ex));
            }

            log().error("Failed to parse received response.", ex);
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


    Collection<T> publish(RabbitTemplate rabbit, String queue, AbstractMessageListenerContainer listener) {

        listener.setMessageListener(this);
        publishQuery(rabbit, queue);

        try {
            Thread.sleep(this.waitingFor.toMillis());
        } catch (InterruptedException e) {
            // TODO: Apply to provided onError-handler
            e.printStackTrace();
        }

        if (results.get().isEmpty()) {
            if (this.orDefaults != null) {
                return this.orDefaults.get();
            }
            // TODO: Or throws, etc.
        }

        return results.get();
    }


    private void publishQuery(RabbitTemplate rabbit, String queue) {

        try {
            var message = MessageBuilder.withBody("{}".getBytes()).setReplyTo(queue).build();

            rabbit.send("queries", this.queryTerm, message);
            log().info("|<-- Published query: {}", this.queryTerm);
        } catch (RuntimeException e) {
            // TODO: Apply to provided onError-handler
            e.printStackTrace();
        }
    }


    static <T> Query<T> valueOf(Queries<T> queries) {

        Query<T> query = new Query<>();

        query.queryTerm = queries.getQueryForTerm();
        query.responseType = queries.getType();
        query.waitingFor = queries.getWaitingFor();
        query.orDefaults = queries.getOrDefaults();
        query.onError = queries.getOnError();

        return query;
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
