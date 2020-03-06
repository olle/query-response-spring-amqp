package com.studiomediatech.queryresponse;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;


/**
 * Providing the entry-point to the fluid builder for responses, through the {@link #respondTo(String)} method.
 *
 * <p>Building a response instance will start a long running query consumer which will respond to any matching queries,
 * for as long as the service is running.</p>
 *
 * @param  <T>  the type of response entries or elements.
 */
public class Responses<T> {

    /**
     * The current implementation supports only term-based queries - that means, there may only be opaque semantics in
     * the given query term. However, the query-term must conform to the AMQP routing-key rules and conventions (and is
     * asserted as an invariant of such).
     */
    private final String respondToTerm;

    /**
     * The declared batch-size for responses. Limiting each published response to the given amount, or less. The
     * batch-size 0 (default) means no-batches and all responses are published at once.
     */
    private int batchSize = 0;

    private Responses(String term) {

        this.respondToTerm = Asserts.invariantQueryTerm(term);
    }

    public static <T> Responses<T> respondTo(String term) {

        return new Responses<>(term);
    }


    public Responses<T> withAll() {

        this.batchSize = 0;

        return this;
    }


    public Responses<T> withBatchesOf(int size) {

        if (size < 1) {
            throw new IllegalArgumentException("Illegal batch size " + size + ", must be positive integer.");
        }

        this.batchSize = size;

        return this;
    }


    @SafeVarargs
    public final void from(T... ts) {

        from(Arrays.asList(ts));
    }


    public void from(Collection<T> set) {

        from(set.stream());
    }


    public void from(Stream<T> stream) {

        RespondingRegistry.register(this, stream);
    }


    public String getTerm() {

        return this.respondToTerm;
    }


    protected int getBatchSize() {

        return batchSize;
    }
}
