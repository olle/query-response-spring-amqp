package com.studiomediatech.responses;

import com.studiomediatech.Responses;

import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


public class ResponsesApiTest {

    @Test
    void ensureExamplesCompile() throws Exception {

        RespondingRegistry.instance = () -> Mockito.mock(RespondingRegistry.class);

        Responses.respondTo("authors")
            .withAll()
            .from("William Gibson", "Isaac Asimov", "J.R.R. Tolkien");

        Responses.respondTo("cars")
            .withPairs()
            .from(Set.of("volvo", "tesla", "nissan", "saab", "opel", "bmw"));

        Responses.respondTo("offers/monday")
            .withBatchesOf(20)
            .from(Offers.findAllOffersByDayOfWeek(Calendar.MONDAY));

        assertThat("no problems").isNotEmpty();
    }

    private static class Offers {

        public static Collection<?> findAllOffersByDayOfWeek(int dayOfWeek) {

            return Collections.emptyList();
        }
    }
}
