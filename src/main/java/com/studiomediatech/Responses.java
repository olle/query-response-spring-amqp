package com.studiomediatech;

import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Stream;


public final class Responses<T> {

    private final String term;

    private Responses(String term) {

        this.term = term;
    }

    public static <T> Responses<T> respondTo(String term) {

        return new Responses<>(term);
    }


    public Responses<T> withAll() {

        // TODO Auto-generated method stub
        return this;
    }


    public Responses<T> withPairs() {

        // TODO Auto-generated method stub
        return this;
    }


    public Responses<T> withBatchesOf(int batch) {

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
