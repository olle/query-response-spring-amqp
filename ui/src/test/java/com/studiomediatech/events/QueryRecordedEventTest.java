package com.studiomediatech.events;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class QueryRecordedEventTest {

    @ParameterizedTest
    @ValueSource(strings = { "some-query", "some-query  ", " some-query", "  some-query    " })
    void ensureGetsQueryTermFromQueryWithoutWhitespace(String arg) {

        assertThat(QueryRecordedEvent.valueOf(arg, "").getQuery()).isEqualTo("some-query");
    }

    @Test
    void ensureGetsQueryTermFromQueryWithArgsSeparatedByWhitespace() {

        assertThat(QueryRecordedEvent.valueOf("some-query 343", "").getQuery()).isEqualTo("some-query");
    }

    @Test
    void ensureGetsTimeoutFromSecondArgAfterWhitespace() throws Exception {

        assertThat(QueryRecordedEvent.valueOf("some-query 343", "").getTimeout()).isEqualTo(343L);
    }

    @Test
    void ensureGetsTimeoutFromSecondArgAfterWhitespaceWithTrailingWhitespace() throws Exception {

        assertThat(QueryRecordedEvent.valueOf("some-query 343  ", "").getTimeout()).isEqualTo(343L);
    }

    @Test
    void ensureGetsDefaultTimeoutIfNoSecondArgAfterWhitespace() throws Exception {

        assertThat(QueryRecordedEvent.valueOf("some-query  ", "").getTimeout()).isEqualTo(150L);
    }

    @Test
    void ensureGetsDefaultTimeoutIfNoSecondArg() throws Exception {

        assertThat(QueryRecordedEvent.valueOf("some-query", "").getTimeout()).isEqualTo(150L);
    }

    @Test
    void ensureHasNoLimitIfNoThirdArgument() throws Exception {

        assertThat(QueryRecordedEvent.valueOf("some-query 343  ", "").getLimit()).isEmpty();
    }

    @Test
    void ensureGetsNonEmptyLimitIfValidThirdArgument() throws Exception {

        assertThat(QueryRecordedEvent.valueOf("some-query 343 12", "").getLimit()).isNotEmpty();
    }

    @Test
    void ensureGetsGivenLimitIfValidThirdArgument() throws Exception {

        assertThat(QueryRecordedEvent.valueOf("some-query 343 12", "").getLimit()).hasValue(12);
    }
}
