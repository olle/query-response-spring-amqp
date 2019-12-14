package com.studiomediatech;

import java.util.function.Supplier;


public final class Query<T> {

    private Query(String term) {

        // Hidden.
    }

    public static <T> Query<T> queryFor(String term) {

        return new Query(term);
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
