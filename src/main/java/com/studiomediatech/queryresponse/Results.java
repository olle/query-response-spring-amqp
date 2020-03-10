package com.studiomediatech.queryresponse;

import java.util.Collection;
import java.util.Collections;


/**
 * Encapsulates the consumed results of published a {@link Queries query}.
 *
 * @param  <T>  type of the elements or entries.
 */
public class Results<T> {

    private final Collection<T> elements;

    protected Results(Collection<T> elements) {

        this.elements = elements;
    }

    public static <T> Results<T> empty() {

        return new EmptyResults<>(Collections.emptyList());
    }


    public Collection<T> accept(Queries<T> queries) {

        if (this instanceof EmptyResults) {
            if (queries.getOrDefaults() != null) {
                return queries.getOrDefaults().get();
            }
            // TODO: Or throws, etc.
        }

        return this.elements;
    }

    static class EmptyResults<T> extends Results<T> {

        protected EmptyResults(Collection<T> elements) {

            super(elements);
        }
    }
}
