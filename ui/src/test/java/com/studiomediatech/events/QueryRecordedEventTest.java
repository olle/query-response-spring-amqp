package com.studiomediatech.events;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QueryRecordedEventTest {

    @Test
    void ensureGetsQueryTermFromQueryWithoutWhitespace() {

        assertThat(QueryRecordedEvent.valueOf("some-query", "").getQuery()).isEqualTo("some-query");
    }

    @Test
    void ensureGetsQueryTermFromQueryWithTrailingWhitespace() {

        assertThat(QueryRecordedEvent.valueOf("some-query  ", "").getQuery()).isEqualTo("some-query");
    }

    @Test
    void ensureGetsQueryTermFromQueryWithLeadingWhitespace() {

        assertThat(QueryRecordedEvent.valueOf(" some-query", "").getQuery()).isEqualTo("some-query");
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
