package com.studiomediatech.queryresponse;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertThrows;

class AssertsTest {

    @Test
    void ensureReturnsValidQueryTerm() throws Exception {

        assertThat(Asserts.invariantQueryTerm("Hello World!")).isEqualTo("Hello World!");
    }

    @Test
    void ensureThrowsOnTooLongQueryTerm() {

        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 256; i++) {
            sb.append("a");
        }

        assertThrows(IllegalArgumentException.class, () -> Asserts.invariantQueryTerm(sb.toString()));
    }

    @Test
    void ensureThrowsForNullQueryTerm() throws Exception {

        assertThrows(IllegalArgumentException.class, () -> Asserts.invariantQueryTerm(null));
    }

    @Test
    void ensureThrowsForEmptyQueryTerm() throws Exception {

        assertThrows(IllegalArgumentException.class, () -> Asserts.invariantQueryTerm(""));
    }

    @Test
    void ensureThrowsForWhitespaceQueryTerm() throws Exception {

        assertThrows(IllegalArgumentException.class, () -> Asserts.invariantQueryTerm("     "));
    }

    @Test
    void ensureValidDurationIsPassedThrough() throws Exception {

        assertThat(Asserts.invariantDuration(Duration.ofMillis(1234))).isEqualByComparingTo(Duration.ofMillis(1234));
    }

    @Test
    void ensureThrowsForZeroDuration() throws Exception {

        assertThrows(IllegalArgumentException.class, () -> Asserts.invariantDuration(Duration.ofMillis(0L)));
    }

    @Test
    void ensureThrowsForNegativeDuration() throws Exception {

        assertThrows(IllegalArgumentException.class, () -> Asserts.invariantDuration(Duration.ofMillis(-2L)));
    }

    @Test
    void ensureThrowsForExcessiveLongDuration() throws Exception {

        assertThrows(IllegalArgumentException.class,
                () -> Asserts.invariantDuration(Duration.ofMillis(Long.MAX_VALUE).plusMillis(1)));
    }

    @Test
    void ensureThrowsForNegativeTakingAtMost() throws Exception {

        assertThrows(IllegalArgumentException.class, () -> Asserts.invariantAtMost(-1));
    }

    @Test
    void ensureThrowsForZeroTakingAtMost() throws Exception {

        assertThrows(IllegalArgumentException.class, () -> Asserts.invariantAtMost(0));
    }

    @Test
    void ensureThrowsForNegativeTakingAtLeast() throws Exception {

        assertThrows(IllegalArgumentException.class, () -> Asserts.invariantAtLeast(-1));
    }

    @Test
    void ensureThrowsForZeroTakingAtLeast() throws Exception {

        assertThrows(IllegalArgumentException.class, () -> Asserts.invariantAtLeast(0));
    }

    @Test
    void ensureThrowsForBatchSizeOfZero() throws Exception {

        assertThrows(IllegalArgumentException.class, () -> Asserts.invariantBatchSize(0));
    }

    @Test
    void ensureThrowsForNegativeBatchSize() throws Exception {

        assertThrows(IllegalArgumentException.class, () -> Asserts.invariantBatchSize(-23));
    }

    @Test
    void ensureThrowsForEmptyVarargsArray() throws Exception {

        assertThrows(IllegalArgumentException.class, () -> Asserts.invariantResponseVarargsArray(new Object[] {}));
    }

    @Test
    void ensureThrowsForNullVarargsArray() throws Exception {

        assertThrows(IllegalArgumentException.class, () -> Asserts.invariantResponseVarargsArray((Object[]) null));
    }

    @Test
    void ensureThrowsForNullElementsInVarargs() throws Exception {

        assertThrows(IllegalArgumentException.class,
                () -> Asserts.invariantResponseVarargsArray(new String[] { "hello", null, "fake" }));
    }

    @Test
    void ensureThrowsOnNullSupplier() throws Exception {

        assertThrows(IllegalArgumentException.class, () -> Asserts.invariantSupplier(null));
    }
}
