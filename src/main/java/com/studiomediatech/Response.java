package com.studiomediatech;

import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;


public class Response<T> {

    private final String term;

    private Response(String term) {

        this.term = term;
    }


    public Response() {

        this.term = null;
    }

    public <R, A> R collect(Collector<? super T, A, R> collector) {

        // TODO Auto-generated method stub
        return null;
    }


    public static <T> Response<T> respondTo(String term) {

        return new Response<>(term);
    }


    public Response<T> withAll() {

        // TODO Auto-generated method stub
        return this;
    }


    public Response<T> withPairs() {

        // TODO Auto-generated method stub
        return this;
    }


    public Response<T> withBatchesOf(int batch) {

        // TODO Auto-generated method stub
        return this;
    }


    public void from(T... ts) {

        // TODO Auto-generated method stub
    }


    public void from(Supplier<T> supplier) {

        // TODO Auto-generated method stub
    }


    public void from(Collection<T> set) {

        // TODO Auto-generated method stub
    }


    public void from(Stream<T> stream) {

        // TODO Auto-generated method stub
    }
}
