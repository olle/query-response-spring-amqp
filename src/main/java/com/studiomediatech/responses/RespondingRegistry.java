package com.studiomediatech.responses;

import com.studiomediatech.Responses;

import org.springframework.beans.BeansException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.function.Supplier;
import java.util.stream.Stream;


public class RespondingRegistry implements ApplicationContextAware {

    // WARNING: Non-exemplary use of static supplier, for lazy access to bean instance.
    protected static Supplier<RespondingRegistry> instance = () -> null;

    public static <T> void register(Responses<T> responses, Stream<T> stream) {

        var registry = instance.get();

        if (registry == null) {
            throw new IllegalStateException("No registry is initialized.");
        }

        System.out.println("Adding responses " + responses + " to registry " + registry);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        RespondingRegistry.instance = () -> applicationContext.getBean(RespondingRegistry.class);
    }
}
