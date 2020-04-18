package com.studiomediatech.queryresponse;

import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;


public class ResponseBuilderApiTest {

    @Test
    void ex1() {

        ResponseRegistry.instance = () -> Mockito.mock(ResponseRegistry.class);

        ResponseBuilder.respondTo("authors", String.class)
            .withAll()
            .from("William Gibson", "Isaac Asimov", "J.R.R. Tolkien");
    }


    @Test
    void ex2() throws Exception {

        Offers offers = new Offers();

        ResponseBuilder.respondTo("offers/monday", Offer.class)
            .withBatchesOf(20)
            .from(offers.findAllOffersByDayOfWeek(Calendar.MONDAY));
    }


    @Test
    void ex3() throws Exception {

        UserTokens userTokenService = new UserTokens();

        ResponseBuilder.respondTo("users/current", Token.class)
            .withBatchesOf(256)
            .suppliedBy(userTokenService::findAllCurrentUserTokens);
    }

    private static class Offers {

        public Collection<Offer> findAllOffersByDayOfWeek(int dayOfWeek) {

            return Collections.emptyList();
        }
    }

    static class Offer {

        // OK
    }

    private static class UserTokens {

        public Collection<Token> findAllCurrentUserTokens() {

            return Collections.emptyList();
        }
    }

    static class Token {

        // OK
    }
}
