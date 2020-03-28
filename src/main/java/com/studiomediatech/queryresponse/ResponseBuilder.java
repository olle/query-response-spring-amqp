package com.studiomediatech.queryresponse;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Supplier;


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

    /**
     * The supplier of a total count of elements that a responder may be able to respond with. Can either be explicitly
     * provided during build-time, in order to allow for lazily supplied responses. For known collections the supplier
     * provides {@link Collection#size()}.
     */
    private Supplier<Integer> totalSupplier;

    private Collection<T> elementsCollection;
    private Iterator<T> elementsIterator;

    // For testing
    protected ResponseBuilder(String term) {

        this.respondToTerm = Asserts.invariantQueryTerm(term);
    }


    // For testing
    protected ResponseBuilder(String term, Collection<T> ts) {

        this(term);
        this.elementsCollection = ts;
        this.totalSupplier = this.elementsCollection::size;
    }


    // For testing
    protected ResponseBuilder(String term, Iterator<T> it, Supplier<Integer> total) {

        this(term);
        this.elementsIterator = it;
        this.totalSupplier = total;
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

        this.elementsCollection = Asserts.invariantResponseVarargsArray(ts);
        this.totalSupplier = this.elementsCollection::size;

        register();
    }


    public void from(Collection<T> ts) {

        this.elementsCollection = Asserts.invariantResponseCollection(ts);
        this.totalSupplier = this.elementsCollection::size;

        register();
    }


    public void from(Iterator<T> it, Supplier<Integer> total) {

        this.elementsIterator = it;
        this.totalSupplier = total;

        register();
    }


    private void register() {

        ResponseRegistry.register(this);
    }


    String getTerm() {

        return this.respondToTerm;
    }


    int getBatchSize() {

        return this.batchSize;
    }


    Supplier<Integer> getTotalSupplier() {

        return this.totalSupplier;
    }


    Collection<T> getElementsCollection() {

        return this.elementsCollection;
    }


    Iterator<T> getElementsIterator() {

        return this.elementsIterator;
    }
}
