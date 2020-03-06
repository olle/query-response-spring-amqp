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
     * Whether to respond with all provided response entries, at once or not.
     */
    private boolean all = false;

    /**
     * Whether to respond with the provided response entries as pairs, in multiple responses.
     */
    private boolean pairs = false;

    /**
     * A specific batch-size for responses, where 0 means, no batches.
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
        this.all = true;

        return this;
    }


    public Responses<T> withPairs() {

        this.batchSize = 2;
        this.pairs = true;

        return this;
    }


    public Responses<T> withBatchesOf(int size) {

        if (size < 1) {
            throw new IllegalArgumentException("Illegal batch size " + size + ", must be 1 or higher.");
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


    protected boolean isAll() {

        return all;
    }


    protected boolean isPairs() {

        return pairs;
    }


    protected int getBatchSize() {

        return batchSize;
    }
}
