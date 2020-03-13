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

import java.time.Duration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
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
    private final String queueName;

    protected String queryTerm;
    protected Class<T> responseType;
    protected Duration waitingFor;
    protected Supplier<Collection<T>> orDefaults;
    protected Consumer<Throwable> onError;

    // Declared protected, for access in unit tests.
    protected Query() {

        this.results = new AtomicReference<>(Collections.emptyList());
        this.queueName = UUID.randomUUID().toString();
    }

    @Override
    public void onMessage(Message message) {

        log().info("|--> Received response message: {}", message.getMessageProperties());
        handleResponseEnvelope(parseMessage(message));
    }


    private ConsumedResponseEnvelope<T> parseMessage(Message message) {

        try {
            var type = TypeFactory.defaultInstance()
                    .constructParametricType(ConsumedResponseEnvelope.class, responseType);
            ConsumedResponseEnvelope<T> response = reader.forType(type).readValue(message.getBody());
            log().debug("Received response: {}", response);

            return response;
        } catch (IOException ex) {
            if (onError != null) {
                var errorMessage = "Failed to parse response to elements of type " + responseType.getSimpleName();
                onError.accept(new IllegalArgumentException(errorMessage, ex));
            }

            log().error("Failed to parse received response.", ex);
        }

        return ConsumedResponseEnvelope.empty();
    }


    void handleResponseEnvelope(ConsumedResponseEnvelope<T> envelope) {

        if (envelope.elements.isEmpty()) {
            log().warn("Received empty response: {}", envelope);

            return;
        }

        results.set(envelope.elements);
    }


    Collection<T> publish(RabbitTemplate rabbit) {

        publishQuery(rabbit);

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


    private void publishQuery(RabbitTemplate rabbit) {

        try {
            var message = MessageBuilder.withBody("{}".getBytes()).setReplyTo(this.queueName).build();

            rabbit.send("queries", this.queryTerm, message);
            log().info("|<-- Published query: {}", this.queryTerm);
        } catch (RuntimeException ex) {
            if (this.onError != null) {
                this.onError.accept(ex);
            }

            log().error("Failed to publish query message", ex);
        }
    }


    static <T> Query<T> from(QueryBuilder<T> queryBuilder) {

        Query<T> query = new Query<>();

        query.queryTerm = queryBuilder.getQueryForTerm();
        query.responseType = queryBuilder.getType();
        query.waitingFor = queryBuilder.getWaitingFor();
        query.orDefaults = queryBuilder.getOrDefaults();
        query.onError = queryBuilder.getOnError();

        return query;
    }


    public String getQueueName() {

        return this.queueName;
    }


    public Collection<T> accept(RabbitFacade facade) {

        return publish(facade.getRabbitTemplate());
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
