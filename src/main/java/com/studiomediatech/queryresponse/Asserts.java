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
}
