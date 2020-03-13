package com.studiomediatech.queryresponse;

import com.studiomediatech.queryresponse.util.Logging;

import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;

import org.springframework.beans.BeansException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Collection;
import java.util.function.Supplier;


/**
 * Provides a way to register and execute queries.
 */
class QueryRegistry implements ApplicationContextAware, Logging {

    /**
     * The current implementation version of Query/Response declares the globally common exchange name here, as a
     * convention.
     *
     * <p>NOTE: In a future version, the intent is of course to allow users to modify this by some means of
     * configuration.</p>
     */
    private static final String QUERIES_EXCHANGE = "queries";

    // WARNING: Non-exemplary use of static supplier, for lazy access to bean instance.
    protected static Supplier<QueryRegistry> instance = () -> null;

    private final RabbitAdmin rabbitAdmin;
    private final DirectMessageListenerContainer listener;
    private final RabbitTemplate rabbitTemplate;

    public QueryRegistry(RabbitAdmin rabbitAdmin, DirectMessageListenerContainer listener,
        RabbitTemplate rabbitTemplate) {

        this.rabbitAdmin = rabbitAdmin;
        this.listener = listener;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        QueryRegistry.instance = () -> applicationContext.getBean(QueryRegistry.class);
    }


    /**
     * Accepts a queries configuration (builder), ensuring that a {@link Query query} is created, initialized,
     * registered as a message listener and executed.
     *
     * @param  <T>  the type of elements in the returned collection
     * @param  queries  configuration to use when initializing the {@link Query query} to register
     *
     * @return  the collection of results from the executed query, which may be empty based on the configuration, but
     *          it never returns {@code null}
     *
     * @throws  Throwable  an optionally configured unchecked {@link Throwable exception} or an
     *                     {@link IllegalStateException} if the query registry could not be resolved, at the time of
     *                     the call.
     */
    static <T> Collection<T> register(Queries<T> queries) {

        var registry = instance.get();

        if (registry == null) {
            throw new IllegalStateException("No registry is initialized.");
        }

        return registry.accept(queries);
    }


    /*
     * Declared protected, for access in unit-tests.
     */
    protected <T> Collection<T> accept(Queries<T> queries) {

        ensureDeclaredQueriesExchange();

        var queueName = declareQueryResponseQueue();

        try {
            var query = Query.valueOf(queries);

            return query.publish(rabbitTemplate, queueName, listener);
        } finally {
            if (listener.removeQueueNames(queueName)) {
                rabbitAdmin.deleteQueue(queueName);
            }
        }
    }


    private String declareQueryResponseQueue() {

        var queueName = rabbitAdmin.declareQueue().getActualName();
        listener.addQueueNames(queueName);

        return queueName;
    }


    private Exchange ensureDeclaredQueriesExchange() {

        Exchange exchange = log(ExchangeBuilder.topicExchange(QUERIES_EXCHANGE).autoDelete().build());
        rabbitAdmin.declareExchange(exchange);

        return exchange;
    }
}
