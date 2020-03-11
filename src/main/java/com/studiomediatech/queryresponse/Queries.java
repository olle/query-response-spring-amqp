package com.studiomediatech.queryresponse;

import java.time.Duration;
import java.time.temporal.TemporalUnit;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 * Providing the entry-point to the fluid builder for queries, through the {@link #queryFor(String)} method.
 *
 * <p>A {@link Queries queries-instance} is a container for a composed or configured query. It is is much like a
 * command-pattern object, providing all the properties required in order to publish the query, await responses and
 * return the results.</p>
 *
 * @param  <T>  the <em>coerced</em> type of the query's response element {@link Collection collection}.
 */
public final class Queries<T> {

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
    private Supplier<Throwable> orThrows;

    /**
     * Defines the lower bounds predicate for a query, may be {@code null}.
     */
    private Integer takingAtMost;

    /**
     * Defines the upper bounds predicate for a query, may be {@code null}.
     */
    private Integer takingAtLeast;

    /**
     * An optional consumer of throwables, allowing clients to inspect any failures occurring during a query
     * life-cycle.
     */
    private Consumer<OnErrorThrowable> onError;

    private Queries(String term, Class<T> type) {

        this.type = type;
        this.queryForTerm = Asserts.invariantQueryTerm(term);
    }

    /**
     * Creates a query-builder for the given term with an expected, and type coerced, collection of results to be
     * returned.
     *
     * @param  <T>  type of the result elements.
     * @param  term  to query for
     * @param  type  expected to be coerced from responses into collection elements
     *
     * @return  a new queries builder, never {@code null}
     */
    public static <T> Queries<T> queryFor(String term, Class<T> type) {

        return new Queries<>(term, type);
    }


    /**
     * Set the duration, in milliseconds, to block for when issuing a query.
     *
     * @param  millis  duration to block on the calling thread
     *
     * @return  the query builder, for chaining further calls
     */
    public Queries<T> waitingFor(long millis) {

        this.waitingFor = Asserts.invariantDuration(Duration.ofMillis(millis));

        return this;
    }


    /**
     * Set the duration and temporal unit, to block for when issuing a query.
     *
     * @param  amount  the amount of the duration to block on the calling thread
     * @param  timeUnit  the unit of the blocking duration
     *
     * @return  the query builder, for chaining further calls
     */
    public Queries<T> waitingFor(long amount, TemporalUnit timeUnit) {

        this.waitingFor = Asserts.invariantDuration(Duration.of(amount, timeUnit));

        return this;
    }


    /**
     * Set the duration to block for when issuing a query.
     *
     * @param  duration  to block on the calling thread
     *
     * @return  the query builder, for chaining further calls
     */
    public Queries<T> waitingFor(Duration duration) {

        this.waitingFor = Asserts.invariantDuration(duration);

        return this;
    }


    /**
     * Sets the maximum number of elements to consume from responses, effectively limiting the returned results.
     *
     * @param  atMost  number of elements to consume, must be greater than 0
     *
     * @return  the query builder, for chaining further calls
     */
    public Queries<T> takingAtMost(int atMost) {

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
    public Queries<T> takingAtLeast(int atLeast) {

        this.takingAtLeast = Asserts.invariantAtLeast(atLeast);
        assertTakingAtMostAndAtLeast();

        return this;
    }


    /**
     * Sets a handler, to receive any errors raised during execution of the query.
     *
     * @param  handler  to receive the {@link OnErrorThrowable error}
     *
     * @return  the query builder, for chaining further calls
     */
    public Queries<T> onError(Consumer<OnErrorThrowable> handler) {

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
     * Sets the default results to use, for a failing query.
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
     * Sets the default results provider to use, for a failing query. The supplier is only invoked if the query fails.
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
    public Collection<T> orThrow(Supplier<Throwable> throwable) {

        this.orThrows = throwable;

        return register();
    }


    /*
     * NOTE: Callers of this method are explicit, and not wrapped or overload in
     *       calls to themselves, in order to show developers which methods
     *       represent terminal-state in the fluid builder API.
     *
     *       See for yourself. Lookup calling methods.
     */
    private Collection<T> register() {

        // TODO: Assert query state, and pre-process for registering
        assertTakingAtMostAndAtLeast();

        return QueryingRegistry.register(this);
    }


    private void assertTakingAtMostAndAtLeast() {

        var atLeast = Optional.ofNullable(takingAtLeast).orElse(0);
        var atMost = Optional.ofNullable(takingAtMost).orElse(0);

        if (atLeast != 0 && atLeast > atMost && atMost != 0) {
            throw new IllegalArgumentException("Cannot require more than limit. "
                + "atLeast: " + atLeast + ", atMost: " + atMost);
        }

        if (atLeast != 0 && atLeast == atMost && atMost != 0) {
            throw new IllegalArgumentException("Unecessary to require same amount as limit");
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


    Consumer<OnErrorThrowable> getOnError() {

        return this.onError;
    }


    Supplier<Collection<T>> getOrDefaults() {

        return this.orDefaults;
    }


    Supplier<Throwable> getOrThrows() {

        return orThrows;
    }
}
