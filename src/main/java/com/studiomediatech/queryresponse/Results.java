package com.studiomediatech.queryresponse;

import java.util.ArrayList;
import java.util.stream.Collector;
import java.util.stream.Stream;


/**
 * Encapsulates the consumed results of published a {@link Queries query}.
 *
 * @param  <T>  type of the elements or entries.
 */
public class Results<T> {

    private final Stream<T> elements;

    protected Results(Stream<T> elements) {

        this.elements = elements;
    }

    public static <T> Results<T> empty() {

        var empty = new ArrayList<T>();

        return new EmptyResults<>(empty.stream());
    }


    public <R, A> R collect(Collector<? super T, A, R> collector) {

        return elements.collect(collector);
    }


    public Results<T> accept(Queries<T> queries) {

        if (this instanceof EmptyResults) {
            if (queries.getOrDefaults() != null) {
                return new Results<>(queries.getOrDefaults().get());
            }
        }

        return this;
    }

    static class EmptyResults<T> extends Results<T> {

        protected EmptyResults(Stream<T> elements) {

            super(elements);
        }
    }
}
