package com.studiomediatech.queryresponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import java.time.temporal.ChronoUnit;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.when;


@SuppressWarnings("unused")
@ExtendWith(MockitoExtension.class)
public class QueriesApiTest {

    @Mock
    QueryingRegistry registry;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setup() {

        when(registry.accept(any(Queries.class))).thenReturn(Results.empty());
        QueryingRegistry.instance = () -> registry;
    }


    @Test
    void ex1() throws Exception {

        var authors = Queries.queryFor("authors", String.class)
                .waitingFor(800)
                .orEmpty()
                .collect(Collectors.toList());
    }


    @Test
    void ex2() throws Exception {

        var authors = Queries.queryFor("authors", String.class)
                .waitingFor(800)
                .orDefaults(Authors.defaults())
                .collect(Collectors.toList());
    }


    @Test
    void ex3() throws Exception {

        var authors = Queries.queryFor("authors", String.class)
                .takingAtMost(10)
                .waitingFor(800)
                .orDefaults(Authors.defaults())
                .collect(Collectors.toList());
    }


    @Test
    void ex4() throws Exception {

        var offers = Queries.queryFor("offers/rental", Offer.class)
                .takingAtLeast(10)
                .takingAtMost(20)
                .waitingFor(2, ChronoUnit.SECONDS)
                .orThrow(TooFewOffersConstraintException::new)
                .collect(Collectors.toList());
    }

    private static class Authors {

        public static Collection<String> defaults() {

            return Collections.emptyList();
        }
    }

    private static class Offer {

        // OK
    }

    private static class TooFewOffersConstraintException extends RuntimeException {

        private static final long serialVersionUID = 1L;
    }
}
