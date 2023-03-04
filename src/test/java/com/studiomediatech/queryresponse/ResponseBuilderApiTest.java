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

        Offers offers = new Offers();
        UserTokens userTokenService = new UserTokens();

        // EXAMPLES --------

        ChainingResponseBuilder.respondTo("authors", String.class).withAll().from("William Gibson", "Isaac Asimov",
                "J.R.R. Tolkien");

        ChainingResponseBuilder.respondTo("offers/monday", Offer.class).withBatchesOf(20)
                .from(offers.findAllOffersByDayOfWeek(Calendar.MONDAY));

        ChainingResponseBuilder.respondTo("users/current", Token.class).withBatchesOf(256)
                .suppliedBy(userTokenService::findAllCurrentUserTokens);

        // CLEANUP --------

        ResponseRegistry.instance = () -> null;
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
