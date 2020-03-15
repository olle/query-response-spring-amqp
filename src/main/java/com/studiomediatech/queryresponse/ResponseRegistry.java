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

    public ResponseRegistry(RabbitFacade facade) {

        this.facade = facade;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        ResponseRegistry.instance = () -> applicationContext.getBean(getClass());
    }


    public static <T> void register(ResponseBuilder<T> responses) {

        var registry = instance.get();

        if (registry == null) {
            throw new IllegalStateException("No registry is initialized.");
        }

        registry.accept(responses);
    }


    protected <T> void accept(ResponseBuilder<T> responses) {

        var response = Response.valueOf(responses);

        facade.declareQueue(response);
        facade.declareBinding(response);
        facade.createMessageListenerContainer(response);

        response.accept(facade);

        log().info("Registered response {}", response);
    }
}
