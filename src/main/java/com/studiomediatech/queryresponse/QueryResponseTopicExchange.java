package com.studiomediatech.queryresponse;

import org.springframework.amqp.core.TopicExchange;

/**
 * Custom topic exchange type.
 */
public class QueryResponseTopicExchange extends TopicExchange {

    private static final boolean DURABLE = false;
    private static final boolean AUTO_DELETE = true;

    /**
     * Creates a new instance of the query response topic exchange.
     *
     * @param name
     *            of the exchange
     */
    public QueryResponseTopicExchange(String name) {

        super(name, DURABLE, AUTO_DELETE);
    }

}
