package com.studiomediatech.queryresponse;

import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;


public class ResponsesApiTest {

    @Test
    void ensureExamplesCompile() throws Exception {

        ResponseRegistry.instance = () -> Mockito.mock(ResponseRegistry.class);

        Responses.respondTo("authors")
            .withAll()
            .from("William Gibson", "Isaac Asimov", "J.R.R. Tolkien");

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
