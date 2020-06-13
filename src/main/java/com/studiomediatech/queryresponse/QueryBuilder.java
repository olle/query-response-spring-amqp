package com.studiomediatech.queryresponse;

public class QueryBuilder {

    private final QueryRegistry queryRegistry;

    public QueryBuilder(QueryRegistry queryRegistry) {

        this.queryRegistry = queryRegistry;
    }

    public <T> YQueryBuilder<T> queryFor(String term, Class<T> type) {

        return YQueryBuilder.queryFor(term, type).withRegistry(queryRegistry);
    }
}
