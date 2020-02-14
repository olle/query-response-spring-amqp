package com.studiomediatech;

import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;


public final class Response<T> {

    private final Stream<T> stream;
    private Supplier<Throwable> supplies;

    protected Response(Stream<T> stream) {

        this.stream = stream;
    }


    public Response(Stream<T> stream, Supplier<Throwable> supplies) {

        this(stream);
        this.supplies = supplies;
    }

    public <R, A> R collect(Collector<? super T, A, R> collector) {

        return stream.collect(collector);
    }
}
