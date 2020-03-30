package com.studiomediatech.queryresponse;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;
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

    enum Mode {

        /**
         * Invariant mode, for when the builder is not determined, in our case the default type after initialization.
         */
        UNKNOWN,

        /**
         * The direct response builder mode, will provide and encapsulate the response elements in a collection that
         * can be used <em>directly</em>. The available collection can be used over and over again, when publishing
         * responses. Most typically this means use of heap-space. It is simple and works.
         */
        DIRECT;
    }

    /**
     * Describes the mode of this builder.
     */
    private Mode mode = Mode.UNKNOWN;

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

    /**
     * The target, used in the terminal operation. Can be modified from {@link #withSink(Consumer)}.
     */
    private Consumer<ResponseBuilder<T>> sink = ResponseRegistry::register;

    /**
     * Constructs a response builder, with the given term to respond to.
     *
     * <p>NOTE: This is the only available constructor for a response builder. It is desirable to only provide the
     * builder API, also for use in testing. Developers can use the {@link #withSink(Consumer)} method, to capture the
     * builder, and avoid the call to the registry.</p>
     *
     * @param  term  to respond to
     */
    private ResponseBuilder(String term) {

        this.respondToTerm = Asserts.invariantQueryTerm(term);
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
        this.mode = Mode.DIRECT;

        register();
    }


    public void from(Collection<T> ts) {

        this.elementsCollection = Asserts.invariantResponseCollection(ts);
        this.totalSupplier = this.elementsCollection::size;
        this.mode = Mode.DIRECT;

        register();
    }


    /*
     * This was a bad idea. The wrong idea. That was good to learn.
     *
     * Using an iterator, will only work for one-shot responses, since that API
     * does not allow for the iterator to be reset on successive calls. The
     * intent here is to support some mode (type) with less claim on the heap.
     *
     * Probably a suppler/provider mode.
     */
    public void from(Iterator<T> it, Supplier<Integer> total) {

        this.elementsIterator = it;
        this.totalSupplier = total;

        register();
    }


    private void register() {

        sink.accept(this);
    }


    String getRespondToTerm() {

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


    Mode getMode() {

        return this.mode;
    }


    /**
     * Replaces the current sink for this builder, effectively removing the {@link ResponseRegistry registry} and
     * instead making the terminal operation to apply the builder on the given sink. For example:
     *
     * <pre>
        AtomicReference<ResponseBuilder<String>> capture = new AtomicReference<>(null);

        ResponseBuilder.respondTo("foobar")
            .withSink(capture::set)
            .withAll()
            .from("hello", "world!");

        assertThat(capture.get()).isNotNull();
     * </pre>
     *
     * <p>This method is protected, in order to reduce visibility and only make it available to tests.</p>
     *
     * @param  sink  to consume this builder, in the terminal operation call to any {@code from(...)} method.
     *
     * @return  this builder, for chaining.
     */
    protected ResponseBuilder<T> withSink(Consumer<ResponseBuilder<T>> sink) {

        this.sink = sink;

        return this;
    }
}
