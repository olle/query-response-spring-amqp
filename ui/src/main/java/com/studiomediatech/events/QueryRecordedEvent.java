package com.studiomediatech.events;

public final class QueryRecordedEvent {

    private final String query;
    private final String publisherId;

    private QueryRecordedEvent(String query, String publisherId) {

        this.query = query;
        this.publisherId = publisherId;
    }

    public String getQuery() {

        return query;
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
}
