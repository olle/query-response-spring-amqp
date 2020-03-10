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
 * @param  <T>  the <em>coerced</em> type of the query result's entries or elements.
 */
public class Queries<T> {

    /**
     * The current implementation supports only term-based queries - that means, there may only be opaque semantics in
     * the given query term. However, the query-term must conform to the AMQP routing-key rules and conventions (and is
     * asserted as an invariant of such).
     */
    private final String queryForTerm;

    /**
     * The retained type hint for later coercion of any returned results.
     */
    private final Class<T> type;

    /**
     * The duration to block this query before returning either the gathered results, provided defaults or throw some
     * given exception. The duration is required and must be set to at least a non-negative, non-zero duration of
     * milliseconds.
     */
    private Duration waitingFor;

    /**
     * Optionally supplies a stream of default result entries or elements. The defaults are assumed to be lazily
     * supplied, and therefore wrapped in a supplier, which may return {@code null}, here.
     */
    private Supplier<Collection<T>> orDefaults;

    private Integer takingAtMost;

    private Integer takingAtLeast;

    private Supplier<Throwable> orThrows;

    private Queries(String term, Class<T> type) {

        this.type = type;
        this.queryForTerm = Asserts.invariantQueryTerm(term);
    }

    public static <T> Queries<T> queryFor(String term, Class<T> type) {

        return new Queries<>(term, type);
    }


    /**
     * Retrieves the query term.
     *
     * @return  the query term string value, never {@code null}, empty or blank.
     */
    protected String getQueryForTerm() {

        return this.queryForTerm;
    }


    protected Class<T> getType() {

        return this.type;
    }


    public Queries<T> waitingFor(long millis) {

        this.waitingFor = invariantDuration(Duration.ofMillis(millis));

        return this;
    }


    public Queries<T> waitingFor(long amount, TemporalUnit timeUnit) {

        this.waitingFor = invariantDuration(Duration.of(amount, timeUnit));

        return this;
    }


    public Queries<T> waitingFor(Duration duration) {

        this.waitingFor = invariantDuration(duration);

        return this;
    }


    private Duration invariantDuration(Duration duration) {

        if (duration.isNegative() || duration.isZero()) {
            throw new IllegalArgumentException("Waiting duration must not be negative or 0");
        }

        if (duration.compareTo(Duration.ofMillis(Long.MAX_VALUE)) > 0) {
            throw new IllegalArgumentException("Duration too long, cannot be greater than Long.MAX_VALUE millis");
        }

        return duration;
    }


    /**
     * Retrieves the await/blocking timeout duration.
     *
     * @return  the duration to wait/block for this query, may be {@code null}.
     */
    protected Duration getWaitingFor() {

        return this.waitingFor;
    }


    public Queries<T> takingAtMost(int atMost) {

        this.takingAtMost = invariantAtMost(atMost);
        assertTakingAtMostAndAtLeast();

        return this;
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


    private int invariantAtMost(int atMost) {

        if (atMost < 1) {
            throw new IllegalArgumentException("Taking at most cannot be less than 1");
        }

        return atMost;
    }


    public Queries<T> takingAtLeast(int atLeast) {

        this.takingAtLeast = invariantAtLeast(atLeast);
        assertTakingAtMostAndAtLeast();

        return this;
    }


    private int invariantAtLeast(int atLeast) {

        if (atLeast < 1) {
            throw new IllegalArgumentException("Taking at least cannot be less than 1");
        }

        return atLeast;
    }


    public Queries<T> onError(Consumer<Throwable> handler) {

        // TODO Auto-generated method stub
        return this;
    }


    public Collection<T> orEmpty() {

        return orDefaults(Collections.emptyList());
    }


    public Collection<T> orDefaults(Collection<T> defaults) {

        this.orDefaults = () -> defaults;

        return register();
    }


    protected Supplier<Collection<T>> getOrDefaults() {

        return this.orDefaults;
    }


    public Collection<T> orThrow(Supplier<Throwable> throwable) {

        this.orThrows = throwable;

        return register();
    }


    protected Supplier<Throwable> getOrThrows() {

        return orThrows;
    }


    private Collection<T> register() {

        // TODO: Assert query state, and pre-process for registering
        assertTakingAtMostAndAtLeast();

        return QueryingRegistry.register(this);
    }
}
