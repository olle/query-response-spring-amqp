package com.studiomediatech.queryresponse.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

class LoggableTest {

    @Test
    void ensureExposesLogger() {
        (new Loggable() {
            void test() {
                assertThat(logger()).isNotNull().isInstanceOfAny(Logger.class);
            }
        }).test();
    }

}
