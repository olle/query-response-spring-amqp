package com.studiomediatech.queryresponse;

import com.studiomediatech.queryresponse.util.Logging;

import org.springframework.beans.BeansException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.function.Supplier;


/**
 * Provides a way to create and register responses.
 */
class ResponseRegistry implements ApplicationContextAware, Logging {

    // WARNING: Non-exemplary use of static supplier, for lazy access to bean instance.
    protected static Supplier<ResponseRegistry> instance = () -> null;

    private final RabbitFacade facade;
    private final Statistics stats;

    public ResponseRegistry(RabbitFacade facade, Statistics stats) {

        this.facade = facade;
        this.stats = stats;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        ResponseRegistry.instance = () -> applicationContext.getBean(getClass());
    }


    public static <T> void register(YResponseBuilder<T> responses) {

        var registry = instance.get();

        if (registry == null) {
            throw new IllegalStateException("No registry is initialized.");
        }

        registry.accept(responses);
    }


    <T> void accept(YResponseBuilder<T> responses) {

        doAccept(Response.from(responses));
    }


    protected <T> void doAccept(Response<T> response) {

        try {
            facade.declareQueue(response);
            facade.declareBinding(response);
            facade.addListener(response);

            response.accept(facade, stats);
            log().info("Registered response {}", response);
        } catch (Throwable th) {
            log().error("Failed to register response", th);
            facade.removeListener(response);
            facade.removeQueue(response);
        }
    }
}
