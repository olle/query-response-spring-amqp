package com.studiomediatech.queryresponse;

final class Asserts {

    private Asserts() {

        // Hidden
    }

    public static void notEmpty(String s) {

        if (s == null || s.isEmpty() || s.isBlank()) {
            throw new IllegalArgumentException("The given string argument must not be null, empty or blank.");
        }
    }
}
