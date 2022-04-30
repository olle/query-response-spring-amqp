package com.studiomediatech.queryresponse;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class QueryResponseTopicExchangeTest {

	@Test
	void ensureQueryResponseTopicExchangeProperties() {
		
		QueryResponseTopicExchange sut = new QueryResponseTopicExchange("some-name");

		assertThat(sut.isAutoDelete()).isTrue();
		assertThat(sut.isDurable()).isFalse();
	}
}
