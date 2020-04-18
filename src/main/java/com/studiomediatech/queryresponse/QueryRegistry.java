package com.studiomediatech.queryresponse;

import com.studiomediatech.queryresponse.util.Logging;

import org.springframework.beans.BeansException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Collection;
import java.util.function.Supplier;


/**
 * Provides a way to register and execute queries.
 */
class QueryRegistry implements ApplicationContextAware, Logging {

    // WARNING: Non-exemplary use of static supplier, for lazy access to bean instance.
    protected static Supplier<QueryRegistry> instance = () -> null;

    private final RabbitFacade facade;
    private final Statistics stats;

    public QueryRegistry(RabbitFacade facade, Statistics stats) {

        this.facade = facade;
        this.stats = stats;
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
     * @param  queryBuilder  configuration to use when initializing the {@link Query query} to register
     *
     * @return  the collection of results from the executed query, which may be empty based on the configuration, but
     *          it never returns {@code null}
     *
     * @throws  Throwable  an optionally configured unchecked {@link Throwable exception} or an
     *                     {@link IllegalStateException} if the query registry could not be resolved, at the time of
     *                     the call.
     */
    static <T> Collection<T> register(QueryBuilder<T> queryBuilder) {

        var registry = instance.get();

        if (registry == null) {
            throw new IllegalStateException("No registry is initialized.");
        }

        return registry.accept(queryBuilder);
    }


    /*
     * Declared protected, for access in unit-tests.
     */
    protected <T> Collection<T> accept(QueryBuilder<T> queryBuilder) throws RuntimeException {

        var query = Query.from(queryBuilder);

        facade.declareQueue(query);
        facade.addListener(query);

        try {
            stats.incrementQueriesCounter();

            return query.accept(facade);
        } finally {
            facade.removeListener(query);
            facade.removeQueue(query);
        }
    }
}
