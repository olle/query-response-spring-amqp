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
public class YResponseBuilder<T> {

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
    private Consumer<YResponseBuilder<T>> sink = ResponseRegistry::register;

    /**
     * Constructs a response builder, with the given term to respond to.
     *
     * <p>NOTE: This is the only available constructor for a response builder. It is desirable to only provide the
     * builder API, also for use in testing. Developers can use the {@link #withSink(Consumer)} method, to capture the
     * builder, and avoid the call to the registry.</p>
     *
     * @param  term  to respond to
     */
    private YResponseBuilder(String term, Class<T> type) {

        this.respondToTerm = Asserts.invariantQueryTerm(term);
    }

    public static <T> YResponseBuilder<T> respondTo(String term, Class<T> type) {

        return new YResponseBuilder<>(term, type);
    }


    public YResponseBuilder<T> withAll() {

        this.batchSize = 0;

        return this;
    }


    public YResponseBuilder<T> withBatchesOf(int size) {

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

        var elements = Asserts.invariantResponseVarargsArray(ts);

        this.elements = elements::iterator;

        register();
    }


    public void from(Collection<T> ts) {

        var elements = Asserts.invariantResponseCollection(ts);

        this.elements = elements::iterator;

        register();
    }


    public void from(Supplier<Iterator<T>> elements) {

        this.elements = Asserts.invariantSupplier(elements);

        register();
    }


    public void suppliedBy(Supplier<Collection<T>> supplier) {

        this.elements = () -> (Iterator<T>) supplier.get().iterator();

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
    protected YResponseBuilder<T> withSink(Consumer<YResponseBuilder<T>> sink) {

        this.sink = sink;

        return this;
    }
}
