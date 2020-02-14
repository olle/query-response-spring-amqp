package com.studiomediatech;

import java.time.Duration;
import java.time.temporal.TemporalUnit;

import java.util.Collection;
import java.util.function.Supplier;


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

        // TODO Auto-generated method stub
        return new Response<>();
    }


    public Response<T> orDefaults(Collection<T> defaults) {

        // TODO Auto-generated method stub
        return new Response<>();
    }


    public Response<T> orEmpty() {

        // TODO Auto-generated method stub
        return new Response<>();
    }


    public Response<T> orThrow(Supplier<Throwable> supplies) {

        // TODO Auto-generated method stub
        return new Response<>();
    }
}
