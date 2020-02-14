package com.studiomediatech;

import org.junit.jupiter.api.Test;

import java.time.temporal.ChronoUnit;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


@SuppressWarnings("unused")
public class QueriesApiTest {

    @Test
    void ensureExamplesCompile() throws Exception {

        {
            var authors = Queries.queryFor("authors")
                    .waitingFor(800)
                    .orEmpty()
                    .collect(Collectors.toList());
        }

        {
            var authors = Queries.queryFor("authors")
                    .waitingFor(800)
                    .orDefaults(Authors.defaults())
                    .collect(Collectors.toList());
        }

        {
            var authors = Queries.queryFor("authors")
                    .takingAtMost(10)
                    .waitingFor(800)
                    .orDefaults(Authors.defaults())
                    .collect(Collectors.toList());
        }

        {
            var offers = Queries.queryFor("offers/rental")
                    .takingAtLeast(10)
                    .takingAtMost(20)
                    .waitingFor(2, ChronoUnit.SECONDS)
                    .orThrow(TooFewOffersConstraintException::new)
                    .collect(Collectors.toList());
        }

        assertThat("no problems").isNotEmpty();
    }

    private static class Authors {

        public static Collection<Object> defaults() {

            return Collections.emptyList();
        }
    }

    private static class TooFewOffersConstraintException extends RuntimeException {

        private static final long serialVersionUID = 1L;
    }
}
