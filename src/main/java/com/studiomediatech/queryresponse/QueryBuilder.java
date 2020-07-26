package com.studiomediatech.queryresponse;

import java.util.Collection;


public class QueryBuilder {

    private final QueryRegistry queryRegistry;

    public QueryBuilder(QueryRegistry queryRegistry) {

        this.queryRegistry = queryRegistry;
    }

    /**
     * Creates a new chaining query-builder for the given term, and the expected type that result elements will be
     * mapped to.
     *
     * <p>The query {@code term} string, can use any pattern or structure, as defined by the system design. It
     * expresses some <em>need</em> or interest in information. The given {@code type}, provide the data container,
     * that is used if any responses are consumed.</p>
     *
     * <p>The type, of the eventually resulting {@link Collection} is also inferred by the {@code type} argument. For
     * example:</p>
     *
     * <pre>
           Collection&lt;String&gt; names = QueryBuilder.queryFor("names/rock-legends", String.class)...
     * </pre>
     *
     * @param  <T>  type of the result elements, and the {@code type} argument.
     * @param  term  to query for. A {@link String} of up to 255 characters.
     * @param  type  of response elements. Any Java type or {@code class} to map JSON responses to. Also infers the
     *               type of the returned results {@link Collection} - if the built query succeeds.
     *
     * @return  a new {@link ChainingQueryBuilder} instance, <strong>never {@code null}</strong>
     *
     * @throws  IllegalArgumentException  if the query {@code term} argument is invalid.
     */
    public <T> ChainingQueryBuilder<T> queryFor(String term, Class<T> type) {

        return ChainingQueryBuilder.queryFor(term, type).withRegistry(queryRegistry);
    }
}
