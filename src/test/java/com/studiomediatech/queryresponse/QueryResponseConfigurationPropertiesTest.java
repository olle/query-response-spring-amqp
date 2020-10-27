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


    @Test
    void ensureDefaultQueuePrefix() throws Exception {

        QueryResponseConfigurationProperties props = new QueryResponseConfigurationProperties();

        assertThat(props.getQueue()).isNotNull();
        assertThat(props.getQueue().getPrefix()).isEqualTo("query-response-");
    }
}
