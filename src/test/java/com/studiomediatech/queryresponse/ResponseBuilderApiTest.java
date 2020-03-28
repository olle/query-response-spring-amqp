package com.studiomediatech.queryresponse;

import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;


public class ResponseBuilderApiTest {

    @Test
    void ensureExamplesCompile() {

        ResponseRegistry.instance = () -> Mockito.mock(ResponseRegistry.class);

        ResponseBuilder.respondTo("authors")
            .withAll()
            .from("William Gibson", "Isaac Asimov", "J.R.R. Tolkien");

        ResponseBuilder.respondTo("offers/monday")
            .withBatchesOf(20)
            .from(Offers.findAllOffersByDayOfWeek(Calendar.MONDAY));

        assertThat("no problems").isNotEmpty();
    }

    private static class Offers {

        public static Collection<Offer> findAllOffersByDayOfWeek(int dayOfWeek) {

            return Collections.emptyList();
        }
    }

    static class Offer {

        // OK
    }
}
