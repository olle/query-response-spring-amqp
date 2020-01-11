package com.studiomediatech;

import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Collection;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


public class ResponseApiTest {

    @Test
    void ensureExamplesCompile() throws Exception {

        Response.respondTo("authors")
            .withAll()
            .from("William Gibson", "Isaac Asimov", "J.R.R. Tolkien");

        Response.respondTo("cars")
            .withPairs()
            .from(Set.of("volvo", "tesla", "nissan", "saab", "opel", "bmw"));

        Response.respondTo("offers/monday")
            .withBatchesOf(20)
            .from(Offers.findAllOffersByDayOfWeek(Calendar.MONDAY));

        assertThat("no problems").isNotEmpty();
    }

    private static class Offers {

        public static Collection<?> findAllOffersByDayOfWeek(int dayOfWeek) {

            // TODO Auto-generated method stub
            return null;
        }
    }
}
