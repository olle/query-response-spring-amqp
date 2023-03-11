package com.studiomediatech.events;

import java.util.Optional;

public final class QueryRecordedEvent {

    private static final int DEFAULT_TIMEOUT_MS = 150;

    private final String query;
    private final String publisherId;

    private QueryRecordedEvent(String query, String publisherId) {

        this.query = query.trim();
        this.publisherId = publisherId;
    }

    public String getQuery() {

        return query.split(" ")[0];
    }

    public String getPublisherId() {

        return publisherId;
    }

    public static QueryRecordedEvent valueOf(String query, String publisher) {

        return new QueryRecordedEvent(query, publisher);
    }

    @Override
    public String toString() {

        return "QueryRecordedEvent [query=" + query + ", publisherId=" + publisherId + "]";
    }

    public long getTimeout() {

        try {
            return query.contains(" ") ? Long.parseLong(query.split(" ")[1]) : DEFAULT_TIMEOUT_MS;
        } catch (RuntimeException e) {
            return 1000;
        }
    }

    public Optional<Integer> getLimit() {

        if (!query.contains(" ")) {
            return Optional.empty();
        }

        try {
            int limit = Integer.parseInt(query.split(" ")[2]);

            return limit > 0 ? Optional.of(limit) : Optional.empty();
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }
}
