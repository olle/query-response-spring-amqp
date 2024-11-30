package com.studiomediatech.queryresponse.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.amqp.core.MessageBuilder;

class LoggableTest {

    @Test
    void ensureExposesLogger() {
        (new Loggable() {
            void test() {
                assertThat(logger()).isNotNull().isInstanceOfAny(Logger.class);
            }
        }).test();
    }

    @Test
    void ensureRedactedMessageOnlyLogsByteLengthOfBody() throws Exception {

        var message = MessageBuilder.withBody("{this-is-redacted}".getBytes()).setReplyTo("reply-to").build();

        assertThat(Loggable.toStringRedacted(message)).doesNotContain("{this-is-redacted}").contains("Body:[18]")
                .contains("replyTo=reply-to");
    }

}
