package com.studiomediatech.queryresponse;

public class ResponseBuilder {

    private final ResponseRegistry responseRegistry;

    public ResponseBuilder(ResponseRegistry responseRegistry) {

        this.responseRegistry = responseRegistry;
    }

    public <T> ChainingResponseBuilder<T> respondTo(String term, Class<T> type) {

        return ChainingResponseBuilder.respondTo(term, type).withRegistry(responseRegistry);
    }
}
