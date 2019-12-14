package com.studiomediatech;

import java.util.function.Supplier;


public final class Query<T> {

    private Query() {

        // Hidden.
    }

    public static <T> Query<T> queryFor(String term) {

        // TODO Auto-generated method stub
        return null;
    }


    public Query<T> waitingFor(int millis) {

        // TODO Auto-generated method stub
        return null;
    }


    public Query<T> takingAtLeast(int count) {

        // TODO Auto-generated method stub
        return null;
    }


    public Response<T> orElse(Supplier<Iterable<T>> defaultValueSupplier) {

        // TODO Auto-generated method stub
        return null;
    }


    public Response<T> orElse(Iterable<T> defaultValue) {

        // TODO Auto-generated method stub
        return null;
    }


    public Response<T> orEmpty() {

        // TODO Auto-generated method stub
        return null;
    }
}
