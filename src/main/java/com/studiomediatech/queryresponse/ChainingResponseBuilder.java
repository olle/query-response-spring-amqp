package com.studiomediatech.queryresponse;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 * Providing the entry-point to the fluid builder for responses, through the {@link #respondTo(String, Class)} method.
 *
 * <p>Building a response instance will start a long running query consumer which will respond to any matching queries,
 * for as long as the service is running.</p>
 *
 * @param  <T>  the type of response entries or elements.
 */
public class ChainingResponseBuilder<T> {

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
     * The supplier of the iterator, that provides elements for the built response. As the iterator may be lazily
     * provided, it may be that neither the builder or the created response has any control over the collection backing
     * the response.
     */
    private Supplier<Iterator<T>> elements;

    /**
     * The target, used in the terminal operation. Can be modified from {@link #withSink(Consumer)}.
     */
    private Consumer<ChainingResponseBuilder<T>> sink = ResponseRegistry::register;

    /**
     * Constructs a response builder, with the given term to respond to.
     *
     * <p>NOTE: This is the only available constructor for a response builder. It is desirable to only provide the
     * builder API, also for use in testing. Developers can use the {@link #withSink(Consumer)} method, to capture the
     * builder, and avoid the call to the registry.</p>
     *
     * @param  term  to respond to
     */
    private ChainingResponseBuilder(String term, Class<T> type) {

        this.respondToTerm = Asserts.invariantQueryTerm(term);
    }

    static <T> ChainingResponseBuilder<T> respondTo(String term, Class<T> type) {

        return new ChainingResponseBuilder<>(term, type);
    }


    /**
     * Sets the batching of this builder, to use response entries at once.
     *
     * @return  the response builder, for chaining further calls
     */
    public ChainingResponseBuilder<T> withAll() {

        this.batchSize = 0;

        return this;
    }


    /**
     * Sets the batching size to be used for responses to the given size.
     *
     * @param  size  of each response message, the number of response elements to send in one response
     *
     * @return  the response builder, for chaining further calls
     */
    public ChainingResponseBuilder<T> withBatchesOf(int size) {

        this.batchSize = Asserts.invariantBatchSize(size);

        return this;
    }


    /**
     * Sets the provided varargs of elements, to be used in published responses.
     *
     * @param  elements  the response elements
     */
    @SuppressWarnings("unchecked")
    @SafeVarargs
    public final void from(T... elements) {

        if (elements.length == 1 && elements[0] instanceof Collection) {
            from((Collection<T>) elements[0]);

            return;
        }

        Collection<T> es = Asserts.invariantResponseVarargsArray(elements);

        this.elements = es::iterator;

        register();
    }


    /**
     * Sets the provided collection of elements, to be used in published responses.
     *
     * @param  elements  the response elements collection
     */
    public void from(Collection<T> elements) {

        Collection<T> es = Asserts.invariantResponseCollection(elements);

        this.elements = es::iterator;

        register();
    }


    /**
     * Sets the supplier for an iterator, that can provide elements to be used in published responses.
     *
     * <p>The supplied iterator will be used with respect to the contract of {@link Iterator#hasNext()} and
     * {@link Iterator#next()}, typically only to ensure that batches can be produced, and that the end of the supplied
     * elements can be reached.</p>
     *
     * <p>Please note that memory or resource use, is not specifically covered by this interface. At run-time, a
     * request to supply the iterator, may cause great stress on resource use.</p>
     *
     * @param  elements  supplier of an iterator
     */
    public void from(Supplier<Iterator<T>> elements) {

        this.elements = Asserts.invariantSupplier(elements);

        register();
    }


    /**
     * Sets the supplier for a collection, that can provide elements to be used in published responses.
     *
     * <p>Please note that memory or resource use, is not at all covered by this interface. At run-time, a request to
     * supply the elements collection, could potentially cause great stress on resource use.</p>
     *
     * @param  elements  supplier of a collection
     */
    public void suppliedBy(Supplier<Collection<T>> elements) {

        this.elements = () -> (Iterator<T>) elements.get().iterator();

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


    Supplier<Iterator<T>> elements() {

        return this.elements;
    }


    /**
     * Replaces the current sink for this builder, effectively removing the {@link ResponseRegistry registry} and
     * instead making the terminal operation to apply the builder on the given sink. For example:
     *
     * <pre>
        AtomicReference&lt;ResponseBuilder&lt;String&gt;&gt; capture = new AtomicReference&lt;&gt;(null);

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
    protected ChainingResponseBuilder<T> withSink(Consumer<ChainingResponseBuilder<T>> sink) {

        this.sink = sink;

        return this;
    }


    ChainingResponseBuilder<T> withRegistry(ResponseRegistry responseRegistry) {

        this.sink = responseRegistry::accept;

        return this;
    }
}
