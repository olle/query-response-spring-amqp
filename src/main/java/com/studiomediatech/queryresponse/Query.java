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

import java.io.IOException;

import java.time.Duration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 * Represents an active published query, as the registered message listener for any responses, handling aggregation,
 * fallback, failure delegation and throwing, all depending on the configuration of this query.
 *
 * @param  <T>  expected type of the coerced result elements.
 */
class Query<T> implements MessageListener, Logging {

    private static final long ONE_MILLIS = 1L;

    private static final ObjectReader reader = new ObjectMapper().reader();

    private final String queueName;
    private final List<T> elements;

    protected String queryTerm;
    protected Class<T> responseType;
    protected Duration waitingFor;
    protected Supplier<Collection<T>> orDefaults;
    protected Consumer<Throwable> onError;
    protected int atLeast;
    protected int atMost;
    protected Supplier<RuntimeException> orThrows;

    // Declared protected, for access in unit tests.
    protected Query() {

        this.queueName = UUID.randomUUID().toString();
        this.elements = new ArrayList<>();
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

        elements.addAll(envelope.elements);
    }


    static <T> Query<T> from(QueryBuilder<T> queryBuilder) {

        Query<T> query = new Query<>();

        query.queryTerm = queryBuilder.getQueryForTerm();
        query.responseType = queryBuilder.getType();
        query.waitingFor = queryBuilder.getWaitingFor();
        query.orDefaults = queryBuilder.getOrDefaults();
        query.onError = queryBuilder.getOnError();
        query.atLeast = queryBuilder.getTakingAtLeast();
        query.atMost = queryBuilder.getTakingAtMost();
        query.orThrows = queryBuilder.getOrThrows();

        return query;
    }


    public String getQueueName() {

        return this.queueName;
    }


    public Collection<T> accept(RabbitFacade facade) throws RuntimeException {

        publishQuery(facade);

        var wait = this.waitingFor.toMillis();

        while (wait-- > 0) {
            if (atMost > 0 && elements.size() >= atMost) {
                return this.elements.subList(0, atMost);
            }

            try {
                Thread.sleep(ONE_MILLIS);
            } catch (InterruptedException e) {
                if (onError != null) {
                    onError.accept(new IllegalArgumentException(e));
                }
            }
        }

        if (elements.isEmpty()) {
            if (this.orDefaults != null) {
                return this.orDefaults.get();
            }

            if (this.orThrows != null) {
                throw this.orThrows.get();
            }
        }

        if (atLeast > 0 && elements.size() < atLeast) {
            if (this.orDefaults != null) {
                return this.orDefaults.get();
            }

            if (this.orThrows != null) {
                throw this.orThrows.get();
            }
        }

        return elements;
    }


    private void publishQuery(RabbitFacade facade) {

        try {
            facade.publishQuery(this.queryTerm,
                MessageBuilder.withBody("{}".getBytes()).setReplyTo(this.queueName).build());
        } catch (RuntimeException ex) {
            if (this.onError != null) {
                this.onError.accept(ex);
            }

            log().error("Failed to publish query message", ex);
        }
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
