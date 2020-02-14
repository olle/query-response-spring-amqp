package com.studiomediatech;

public final class Queries<T> {

    public static <T> Query<T> queryFor(String term) {

        return new Query<>(term);
    }
}
