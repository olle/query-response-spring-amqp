package com.studiomediatech.queryresponse;

import java.time.Duration;
import java.time.temporal.TemporalUnit;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * Provides the entry to the fluid builder for queries, with the {@link #queryFor} method.
 *
 * <p>You begin building a query by calling the static method like this:</p>
 *
 * <pre>
       QueryBuilder.queryFor("some-query", SomeType.class)...
 * </pre>
 *
 * <p>A {@link ChainingQueryBuilder queryBuilder-instance} is a container for a composed or configured query. It is is
 * much like a command-pattern object, providing all the properties required in order to publish the query, await
 * responses and return the results.</p>
 *
 * @param  <T>  the <em>coerced</em> type of the query's response element {@link Collection collection}.
 */
public final class ChainingQueryBuilder<T> {

    /**
     * The current implementation supports only term-based queries - that means, there may only be opaque semantics in
     * the given query term. However, the query-term must conform to the AMQP routing-key rules and conventions (and is
     * asserted as an invariant of such).
     */
    private final String queryForTerm;

    /**
     * The retained type hint, used later for coercion of any returned results.
     */
    private final Class<T> type;

    /**
     * The duration to block a query before returning either the gathered results, provided defaults or throw some
     * given exception. The duration is required and must be set to at least a non-negative, non-zero duration of
     * milliseconds.
     */
    private Duration waitingFor;

    /**
     * Optionally supplies a stream of default result entries or elements. The defaults are assumed to be lazily
     * supplied, and therefore wrapped in a supplier.
     */
    private Supplier<Collection<T>> orDefaults;

    /**
     * Optionally supplies a throwable, that is emitted if the query is not fulfilled.
     */
    private Supplier<RuntimeException> orThrows;

    /**
     * Defines the upper bounds predicate for a query, may be {@code null}.
     */
    private int takingAtMost;

    /**
     * Defines the lower bounds predicate for a query, may be {@code null}.
     */
    private int takingAtLeast;

    /**
     * An optional consumer of an {@link Throwable throwable}, allowing clients to inspect any failures occurring
     * during a query life-cycle.
     */
    private Consumer<Throwable> onError;

    /**
     * The target function, applied in the terminal method. Can be modified by {@link #withSink(Consumer)}.
     */
    private Function<ChainingQueryBuilder<T>, Collection<T>> sink = QueryRegistry::register;

    // Declared protected, for access in unit tests.
    protected ChainingQueryBuilder(String term, Class<T> type) {

        this.type = type;
        this.queryForTerm = Asserts.invariantQueryTerm(term);
    }

    static <T> ChainingQueryBuilder<T> queryFor(String term, Class<T> type) {

        return new ChainingQueryBuilder<T>(term, type);
    }


    /**
     * Set the approximate duration, in milliseconds, that the query will block for when being executed.
     *
     * <p>The actual blocking time is not exact. The current contract will ensure blocking for <em>at least</em> the
     * amount of time specified.</p>
     *
     * <p>Queries are always bound to a blocking timeout, and will return after at most the given duration. Combined
     * with the <em>taking...</em> predicates ({@link #takingAtLeast(int)} or {@link #takingAtMost(int)}), this can be
     * used to either return earlier, use a {@link #orDefaults default} or {@link #orThrow(Supplier) fail}.</p>
     *
     * @param  millis  duration to block on the calling thread. At the most {@link Long#MAX_VALUE} milliseconds is
     *                 allowed.
     *
     * @return  the query builder, for chaining further calls
     *
     * @throws  IllegalArgumentException  for any 0, negative or too long duration.
     */
    public ChainingQueryBuilder<T> waitingFor(long millis) {

        this.waitingFor = Asserts.invariantDuration(Duration.ofMillis(millis));

        return this;
    }


    /**
     * Set the duration and temporal unit, to block for when issuing a query.
     *
     * @param  amount  the amount of the duration to block on the calling thread. An amount representing the duration
     *                 of at the most {@link Long#MAX_VALUE} milliseconds is allowed.
     * @param  timeUnit  the unit of the blocking duration
     *
     * @return  the query builder, for chaining further calls
     *
     * @throws  IllegalArgumentException  for any 0, negative or too long duration.
     *
     * @see  #waitingFor(long)
     */
    public ChainingQueryBuilder<T> waitingFor(long amount, TemporalUnit timeUnit) {

        this.waitingFor = Asserts.invariantDuration(Duration.of(amount, timeUnit));

        return this;
    }


    /**
     * Set the duration to block for when issuing a query.
     *
     * @param  duration  to block on the calling thread. A duration of at the most {@link Long#MAX_VALUE} milliseconds
     *                   is allowed.
     *
     * @return  the query builder, for chaining further calls
     *
     * @throws  IllegalArgumentException  for any 0, negative or too long duration.
     *
     * @see  #waitingFor(long)
     */
    public ChainingQueryBuilder<T> waitingFor(Duration duration) {

        this.waitingFor = Asserts.invariantDuration(duration);

        return this;
    }


    /**
     * Sets the maximum number of elements to consume from responses, effectively limiting the returned results.
     *
     * <p>Queries always block for a specified {@link #waitingFor(long) duration}.Combined with this specified amount
     * of maximum elements to consume, the query can be made to return earlier.</p>
     *
     * @param  atMost  number of elements to consume, must be greater than 0.
     *
     * @return  the query builder, for chaining further calls
     *
     * @throws  IllegalArgumentException  for any values less than 1
     */
    public ChainingQueryBuilder<T> takingAtMost(int atMost) {

        this.takingAtMost = Asserts.invariantAtMost(atMost);
        assertTakingAtMostAndAtLeast();

        return this;
    }


    /**
     * Sets the required amount of result elements for the query.
     *
     * @param  atLeast  number of elements required, for the query to be successful, must be an amount greater than 0,
     *                  and cannot be lower than a previous call to {@link #takingAtMost} has set as a limit
     *
     * @return  the query builder, for chaining further calls
     */
    public ChainingQueryBuilder<T> takingAtLeast(int atLeast) {

        this.takingAtLeast = Asserts.invariantAtLeast(atLeast);
        assertTakingAtMostAndAtLeast();

        return this;
    }


    /**
     * Sets a handler, to receive any errors raised during execution of the query.
     *
     * @param  handler  to receive the {@link Throwable error}
     *
     * @return  the query builder, for chaining further calls
     */
    public ChainingQueryBuilder<T> onError(Consumer<Throwable> handler) {

        this.onError = handler;

        return this;
    }


    /**
     * Set the default results, for a failing query, to be an empty collection.
     *
     * @return  the query builder, for chaining further calls
     */
    public Collection<T> orEmpty() {

        this.orDefaults = Collections::emptyList;

        return register();
    }


    /**
     * Sets the default results collection to use, for a failing query.
     *
     * @param  defaults  collection of results to use for a failing query
     *
     * @return  the query builder, for chaining further calls
     */
    public Collection<T> orDefaults(Collection<T> defaults) {

        this.orDefaults = () -> defaults;

        return register();
    }


    /**
     * Sets the default results supplier to use, for a failing query. The supplier is only invoked if the query fails.
     *
     * @param  defaults  collection provider, for results to use if the query fails
     *
     * @return  the query builder, for chaining further calls
     */
    public Collection<T> orDefaults(Supplier<Collection<T>> defaults) {

        this.orDefaults = defaults;

        return register();
    }


    /**
     * Sets a {@link Throwable throwable} supplier, to be invoked in case the query fails.
     *
     * @param  throwable  supplier, invoked to get a throwable to throw in case the query fails
     *
     * @return  the query builder, for chaining further calls
     */
    public Collection<T> orThrow(Supplier<RuntimeException> throwable) {

        this.orThrows = throwable;

        return register();
    }


    /*
     * NOTE: Callers of this method are explicit, and not wrapped or overload in
     * calls to themselves, in order to show developers which methods represent
     * terminal-state in the fluid builder API.
     *
     * See for yourself. Lookup calling methods.
     */
    private Collection<T> register() {

        // TODO: Assert query state, and pre-process for registering
        assertTakingAtMostAndAtLeast();

        return sink.apply(this);
    }


    private void assertTakingAtMostAndAtLeast() {

        int atLeast = Optional.ofNullable(takingAtLeast).orElse(0);
        int atMost = Optional.ofNullable(takingAtMost).orElse(0);

        if (atLeast != 0 && atLeast > atMost && atMost != 0) {
            throw new IllegalArgumentException("Cannot require more than limit. " + "atLeast: " + atLeast
                + ", atMost: " + atMost);
        }

        if (atLeast != 0 && atLeast == atMost) {
            throw new IllegalArgumentException("Unnecessary to require same amount as limit");
        }
    }


    // PACKAGE SCOPED ACCESSORS ------------------------------------------------

    String getQueryForTerm() {

        return this.queryForTerm;
    }


    Class<T> getType() {

        return this.type;
    }


    Duration getWaitingFor() {

        return this.waitingFor;
    }


    Consumer<Throwable> getOnError() {

        return this.onError;
    }


    Supplier<Collection<T>> getOrDefaults() {

        return this.orDefaults;
    }


    Supplier<RuntimeException> getOrThrows() {

        return orThrows;
    }


    int getTakingAtMost() {

        return takingAtMost;
    }


    int getTakingAtLeast() {

        return takingAtLeast;
    }


    /**
     * Replaces the current sink for this builder, effectively removing the {@link QueryRegistry registry} and instead
     * making the terminal operation apply this builder on the provided consumer. Calling this method will
     * short-circuit, and always return {@code null}. However the builder can be captured for tests, as in the example
     * below:
     *
     * <pre>
       AtomicReference&lt;QueryBuilder&lt;String&gt;&gt; capture = new AtomicReference&lt;&gt;(null);

       QueryBuilder.queryFor("foobar", String.class).withSink(capture::set).waitingFor(123).orEmpty();

       assertThat(capture.get()).isNotNull();
     * </pre>
     *
     * <p>This method is protected, in order to reduce visibility and only make it available to tests.</p>
     *
     * @param  sink  to consume this builder, in the terminal operation call to any {@code orXYZ(..)} methods, and
     *               return the results {@code null}.
     *
     * @return  this builder, for chaining.
     */
    protected ChainingQueryBuilder<T> withSink(Consumer<ChainingQueryBuilder<T>> sink) {

        this.sink =
            builder -> {
            sink.accept(builder);

            return null;
        };

        return this;
    }


    ChainingQueryBuilder<T> withRegistry(QueryRegistry queryRegistry) {

        this.sink = queryRegistry::accept;

        return this;
    }
}
