package com.studiomediatech.queryresponse;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class QueryResponseConfigurationPropertiesTest {

    @Test
    void ensureDefaultExchangeName() {

        QueryResponseConfigurationProperties props = new QueryResponseConfigurationProperties();

        assertThat(props.getExchange()).isNotNull();
        assertThat(props.getExchange().getName()).isEqualTo("query-response");
    }
}
