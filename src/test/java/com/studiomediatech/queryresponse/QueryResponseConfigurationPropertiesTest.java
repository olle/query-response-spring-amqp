package com.studiomediatech.queryresponse;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.studiomediatech.queryresponse.QueryResponseConfigurationProperties.ExchangeProperties;
import com.studiomediatech.queryresponse.QueryResponseConfigurationProperties.QueueProperties;
import com.studiomediatech.queryresponse.QueryResponseConfigurationProperties.StatsProperties;

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
        assertThat(sut.getStats().getInitialDelay()).isEqualTo(7000L);
        assertThat(sut.getStats().getDelay()).isEqualTo(11000L);

    }

    @Test
    void ensureCanSetCustomConfiguration() throws Exception {

        QueryResponseConfigurationProperties sut = new QueryResponseConfigurationProperties();

        ExchangeProperties exchangeProps = new ExchangeProperties();
        exchangeProps.setName("other-exchange");

        QueueProperties queueProps = new QueueProperties();
        queueProps.setPrefix("other-prefix");

        StatsProperties statsProps = new StatsProperties();
        statsProps.setTopic("other-topic");
        statsProps.setInitialDelay(123L);
        statsProps.setDelay(1337L);

        sut.setExchange(exchangeProps);
        sut.setQueue(queueProps);
        sut.setStats(statsProps);

        assertThat(sut.getExchange().getName()).isEqualTo("other-exchange");
        assertThat(sut.getQueue().getPrefix()).isEqualTo("other-prefix");
        assertThat(sut.getStats().getTopic()).isEqualTo("other-topic");
        assertThat(sut.getStats().getInitialDelay()).isEqualTo(123L);
        assertThat(sut.getStats().getDelay()).isEqualTo(1337L);
    }
}
