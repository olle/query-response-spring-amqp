package com.studiomediatech;

import java.time.Duration;
import java.time.temporal.TemporalUnit;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;


public final class Query<T> {

    private final String term;

    protected Query(String term) {

        this.term = term;
    }

    public Query<T> waitingFor(int millis) {

        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return this;
    }


    public Query<T> waitingFor(long duration, TemporalUnit timeUnit) {

        try {
            // TODO: Overflow!
            Thread.sleep(Duration.of(duration, timeUnit).toMillis());
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return this;
    }


    public Query<T> takingAtLeast(int count) {

        // TODO Auto-generated method stub
        return this;
    }


    public Query<T> takingAtMost(int count) {

        // TODO Auto-generated method stub
        return this;
    }


    public Response<T> orElse(Supplier<Iterable<T>> defaultValueSupplier) {

        // TODO Auto-generated method stub
        return null;
    }


    public Response<T> orElse(Iterable<T> defaultValue) {

        return new Response<>(StreamSupport.stream(defaultValue.spliterator(), false));
    }


    public Response<T> orDefaults(Collection<T> defaults) {

        return new Response<>(defaults.stream());
    }


    public Response<T> orEmpty() {

        return new Response<>(Collections.<T>emptyList().stream());
    }


    public Response<T> orThrow(Supplier<Throwable> supplies) {

        return new Response<>(Collections.<T>emptyList().stream(), supplies);
    }
}
