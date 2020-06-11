package com.studiomediatech.queryresponse;

public class XQueryBuilder {

    private final QueryRegistry queryRegistry;

    public XQueryBuilder(QueryRegistry queryRegistry) {

        this.queryRegistry = queryRegistry;
    }

    public <T> YQueryBuilder<T> queryFor(String term, Class<T> type) {

        return YQueryBuilder.queryFor(term, type).withRegistry(queryRegistry);
    }
}
