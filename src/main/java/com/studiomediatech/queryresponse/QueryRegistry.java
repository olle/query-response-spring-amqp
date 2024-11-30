package com.studiomediatech.queryresponse;

import com.studiomediatech.queryresponse.util.Loggable;

import org.springframework.beans.BeansException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Provides a way to register and execute queries.
 */
class QueryRegistry implements ApplicationContextAware, Loggable {

    // WARNING: Non-exemplary use of static supplier, for lazy access to bean instance.
    protected static Supplier<QueryRegistry> instance = () -> null;

    private final RabbitFacade facade;
    private final Statistics stats;
    private final QueryResponseConfigurationProperties props;

    public QueryRegistry(RabbitFacade facade, Statistics stats, QueryResponseConfigurationProperties props) {

        this.facade = facade;
        this.stats = stats;
        this.props = props;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        QueryRegistry.instance = () -> applicationContext.getBean(QueryRegistry.class);
    }

    /**
     * Accepts a queries configuration (builder), ensuring that a {@link Query query} is created, initialized,
     * registered as a message listener and executed.
     *
     * @param <T>
     *            the type of elements in the returned collection
     * @param queryBuilder
     *            configuration to use when initializing the {@link Query query} to register
     *
     * @return the collection of results from the executed query, which may be empty based on the configuration, but it
     *         never returns {@code null}
     *
     * @throws Throwable
     *             an optionally configured unchecked {@link Throwable exception} or an {@link IllegalStateException} if
     *             the query registry could not be resolved, at the time of the call.
     */
    static <T> Collection<T> register(ChainingQueryBuilder<T> queryBuilder) {

        QueryRegistry registry = instance.get();

        if (registry == null) {
            throw new IllegalStateException("No registry is initialized.");
        }

        return registry.accept(queryBuilder);
    }

    <T> Collection<T> accept(ChainingQueryBuilder<T> queryBuilder) throws RuntimeException {

        return doAccept(Query.from(queryBuilder, props));
    }

    protected <T> Collection<T> doAccept(Query<T> query) {

        facade.declareQueue(query);
        facade.addListener(query);

        try {
            return query.accept(facade, stats);
        } finally {
            facade.removeListener(query);
            facade.removeQueue(query);
        }
    }
}
