package com.studiomediatech;

import com.studiomediatech.responses.RespondingRegistry;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;


public final class Responses<T> {

    private final String term;
    private boolean all = false;
    private boolean pairs = false;
    private int batchSize = 0;

    private Responses(String term) {

        Asserts.notEmpty(term);

        this.term = term;
    }

    public static <T> Responses<T> respondTo(String term) {

        return new Responses<>(term);
    }


    public Responses<T> withAll() {

        this.all = true;

        return this;
    }


    public Responses<T> withPairs() {

        this.pairs = true;

        return this;
    }


    public Responses<T> withBatchesOf(int batch) {

        this.batchSize = batch;

        return this;
    }


    public void from(T... ts) {

        from(Arrays.asList(ts).stream());
    }


    public void from(Collection<T> set) {

        from(set.stream());
    }


    public void from(Stream<T> stream) {

        RespondingRegistry.register(this, stream);
    }
}
