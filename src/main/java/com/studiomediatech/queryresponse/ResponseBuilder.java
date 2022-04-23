package com.studiomediatech.queryresponse;

/**
 * The main entry type for response building, provides static factory methods to create a
 * {@link ChainingResponseBuilder chaining response builder}.
 */
public class ResponseBuilder {

    private final ResponseRegistry responseRegistry;

    /**
     * Creates a new response builder, using the given registry.
     * 
     * @param responseRegistry for this builder, never {@code null}
     */
    public ResponseBuilder(ResponseRegistry responseRegistry) {

        this.responseRegistry = responseRegistry;
    }

    /**
     * Creates a new chaining response-builder for the given query-term, and the published type of elements.
     *
     * @param  <T>  type of the response elements, and the {@code type} argument.
     * @param  term  the query to respond to. A {@link String} of up to 255 characters.
     * @param  type  of response elements. Any Java type of {@code class} that is published as JSON responses. This
     *               will infer the type of the provided elements.
     *
     * @return  a new {@link ChainingResponseBuilder} instance, <strong>never {@code null}</strong>
     *
     * @throws  IllegalArgumentException  if the response query {@code term} argument is invalid.
     */
    public <T> ChainingResponseBuilder<T> respondTo(String term, Class<T> type) {

        return ChainingResponseBuilder.respondTo(term, type).withRegistry(responseRegistry);
    }
}
