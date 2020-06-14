package com.studiomediatech.queryresponse;

public class QueryBuilder {

    private final QueryRegistry queryRegistry;

    public QueryBuilder(QueryRegistry queryRegistry) {

        this.queryRegistry = queryRegistry;
    }

    public <T> ChainingQueryBuilder<T> queryFor(String term, Class<T> type) {

        return ChainingQueryBuilder.queryFor(term, type).withRegistry(queryRegistry);
    }
}
