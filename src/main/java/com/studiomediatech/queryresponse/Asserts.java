package com.studiomediatech.queryresponse;

import java.time.Duration;


final class Asserts {

    private Asserts() {

        // Hidden
    }

    public static void notEmpty(String s) {

        if (s == null || s.isEmpty() || s.isBlank()) {
            throw new IllegalArgumentException("The given string argument must not be null, empty or blank.");
        }
    }


    public static String invariantQueryTerm(String term) {

        if (term == null) {
            throw new IllegalArgumentException("Query term may not be null");
        }

        if (term.isEmpty() || term.isBlank()) {
            throw new IllegalArgumentException("Query term may not be empty");
        }

        if (term.length() > 255) {
            throw new IllegalArgumentException(""
                + "Query term was too long "
                + "(" + term.length() + ") "
                + "max 255 characters allowed");
        }

        return term;
    }


    public static Duration invariantDuration(Duration duration) {

        if (duration.isNegative() || duration.isZero()) {
            throw new IllegalArgumentException("Waiting duration must not be negative or 0");
        }

        if (duration.compareTo(Duration.ofMillis(Long.MAX_VALUE)) > 0) {
            throw new IllegalArgumentException("Duration too long, cannot be greater than Long.MAX_VALUE millis");
        }

        return duration;
    }


    public static int invariantAtLeast(int atLeast) {

        if (atLeast < 1) {
            throw new IllegalArgumentException("Taking at least cannot be less than 1");
        }

        return atLeast;
    }


    public static int invariantAtMost(int atMost) {

        if (atMost < 1) {
            throw new IllegalArgumentException("Taking at most cannot be less than 1");
        }

        return atMost;
    }
}
