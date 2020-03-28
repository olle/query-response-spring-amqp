package com.studiomediatech.queryresponse;

import java.util.Collection;


/**
 * Providing the entry-point to the fluid builder for responses, through the {@link #respondTo(String)} method.
 *
 * <p>Building a response instance will start a long running query consumer which will respond to any matching queries,
 * for as long as the service is running.</p>
 *
 * @param  <T>  the type of response entries or elements.
 */
public class ResponseBuilder<T> {

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

    private Collection<T> elements;

    // Declared protected, for access in unit tests.
    protected ResponseBuilder(String term) {

        this.respondToTerm = Asserts.invariantQueryTerm(term);
    }


    // Declared protected, for access in unit tests.
    protected ResponseBuilder(String term, Collection<T> ts) {

        this(term);
        this.elements = ts;
    }

    public static <T> ResponseBuilder<T> respondTo(String term) {

        return new ResponseBuilder<>(term);
    }


    public ResponseBuilder<T> withAll() {

        this.batchSize = 0;

        return this;
    }


    public ResponseBuilder<T> withBatchesOf(int size) {

        this.batchSize = Asserts.invariantBatchSize(size);

        return this;
    }


    @SuppressWarnings("unchecked")
    @SafeVarargs
    public final void from(T... ts) {

        if (ts.length == 1 && ts[0] instanceof Collection) {
            from((Collection<T>) ts[0]);

            return;
        }

        this.elements = Asserts.invariantResponseVarargsArray(ts);

        register();
    }


    public void from(Collection<T> ts) {

        this.elements = Asserts.invariantResponseCollection(ts);

        register();
    }


    private void register() {

        ResponseRegistry.register(this);
    }


    String getTerm() {

        return this.respondToTerm;
    }


    int getBatchSize() {

        return batchSize;
    }


    Collection<T> getElements() {

        return this.elements;
    }
}
