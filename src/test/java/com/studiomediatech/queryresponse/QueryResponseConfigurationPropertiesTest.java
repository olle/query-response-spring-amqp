package com.studiomediatech.queryresponse;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;


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
    
    @Test
	void ensureCanConfigureStatsTopic() throws Exception {
		
    	
    	QueryResponseConfigurationProperties sut = new QueryResponseConfigurationProperties();
    	
    	assertThat(sut.getStats()).isNotNull();
    	assertThat(sut.getStats().getTopic()).isEqualTo("query-response/internal/stats");
	}
}
