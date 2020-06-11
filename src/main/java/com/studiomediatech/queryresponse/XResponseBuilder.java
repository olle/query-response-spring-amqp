package com.studiomediatech.queryresponse;

public class XResponseBuilder {

    private final ResponseRegistry responseRegistry;

    public XResponseBuilder(ResponseRegistry responseRegistry) {

        this.responseRegistry = responseRegistry;
    }

    public <T> YResponseBuilder<T> respondTo(String term, Class<T> type) {

        return YResponseBuilder.respondTo(term, type).withRegistry(responseRegistry);
    }
}
