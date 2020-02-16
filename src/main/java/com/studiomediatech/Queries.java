package com.studiomediatech;

import java.time.Duration;


/**
 * Providing the entry-point to the fluid builder for queries, through the {@link #queryFor(String)} method.
 *
 * <p>A {@link Queries queries-instance} is a container for a composed or configured query. It is is much like a
 * command-pattern object, providing all the properties required in order to publish the query, await responses and
 * return the results.</p>
 *
 * @param  <T>  the <em>coerced</em> type of the query result's entries or elements.
 */
public final class Queries<T> {

    /**
     * The current implementation supports only term-based queries - that means, there may only be opaque semantics in
     * the given query term. However, the query-term must conform to the AMQP routing-key rules and conventions (and is
     * asserted as an invariant of such).
     */
    private final String term;

    private Duration duration;

    private Queries(String term) {

        this.term = invariantQueryTerm(term);
    }

    private String invariantQueryTerm(String term) {

        if (term == null) {
            throw new IllegalArgumentException("Query term may not be null");
        }

        if (term.isEmpty() || term.isBlank()) {
            throw new IllegalArgumentException("Query term may not be empty");
        }

        if (term.length() > 255) {
            throw new IllegalArgumentException(""
                + "Query term was too long "
                + "(" + term.length() + ") "
                + "max 255 characters allowed");
        }

        return term;
    }


    public static <T> Queries<T> queryFor(String term) {

        return new Queries<>(term);
    }


    /**
     * Retrieves the query term.
     *
     * @return  the query term string value, never {@code null}, empty or blank.
     */
    public String getTerm() {

        return this.term;
    }


    public Queries<T> waitingFor(long millis) {

        this.duration = invariantDuration(Duration.ofMillis(millis));

        return this;
    }


    private Duration invariantDuration(Duration duration) {

        if (duration.isNegative() || duration.isZero()) {
            throw new IllegalArgumentException("Waiting duration must not be negative or 0");
        }

        return duration;
    }
}
