package com.studiomediatech.queryresponse.ui.messaging;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.studiomediatech.events.QueryRecordedEvent;
import com.studiomediatech.queryresponse.QueryBuilder;
import com.studiomediatech.queryresponse.ui.app.adapter.QueryPublisherAdapter;
import com.studiomediatech.queryresponse.util.Logging;

@Component
public class QueryPublisherPort implements Logging, QueryPublisherAdapter {

    private static final int DEFAULT_QUERY_TIMEOUT = 1500;

    private final QueryBuilder queryBuilder;

    public QueryPublisherPort(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    @Override
    public Map<String, Object> query(String q, int timeout, int limit) {

        List<Object> defaults = List.of("No responses");

        final Collection<Object> responses;

        long start = System.nanoTime();

        if (q.contains(" ")) {
            responses = queryParsed(q, defaults);
        } else {
            responses = queryStrict(q, timeout, limit, defaults);
        }

        return Map.of("response", responses, "duration", Duration.ofNanos(System.nanoTime() - start));
    }

    private Collection<Object> queryParsed(String q, List<Object> defaults) {

        var query = QueryRecordedEvent.valueOf(q, "none");

        query.getQuery();
        query.getTimeout();

        Optional<Integer> maybe = query.getLimit();

        if (maybe.isPresent()) {

            return queryBuilder.queryFor(query.getQuery(), Object.class).waitingFor(query.getTimeout())
                    .takingAtMost(maybe.get()).orDefaults(defaults);

        }

        return queryBuilder.queryFor(query.getQuery(), Object.class).waitingFor(query.getTimeout())
                .orDefaults(defaults);

    }

    private Collection<Object> queryStrict(String q, int timeout, int limit, List<Object> defaults) {
        long queryTimeout = timeout > 0 ? timeout : DEFAULT_QUERY_TIMEOUT;

        if (limit > 0) {
            return queryBuilder.queryFor(q, Object.class).waitingFor(queryTimeout).takingAtMost(limit)
                    .orDefaults(defaults);
        }

        return queryBuilder.queryFor(q, Object.class).waitingFor(queryTimeout).orDefaults(defaults);

    }

}
