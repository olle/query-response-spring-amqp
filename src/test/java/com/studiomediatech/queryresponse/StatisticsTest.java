package com.studiomediatech.queryresponse;

import com.studiomediatech.queryresponse.Statistics.Stat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.context.ApplicationContext;

import org.springframework.mock.env.MockEnvironment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class StatisticsTest {

    @Mock
    ApplicationContext ctx;

    @Mock
    ResponseRegistry registry;

    @Captor
    ArgumentCaptor<ResponseBuilder<?>> responses;

    @SuppressWarnings("static-access")
    @Test
    void ensureBuildsResponseOnApplicationReady() {

        ResponseRegistry.instance = () -> registry;

        var env = new MockEnvironment();

        var sut = new Statistics(env, ctx);
        sut.respond();

        verify(registry).register(responses.capture());

        ResponseBuilder<?> builder = responses.getValue();
        assertThat(builder).isNotNull();

        assertThat(builder.getRespondToTerm()).isEqualTo("query-response/stats");
        assertThat(builder.elements().get()).isNotNull();
        assertThat(builder.elements().get().hasNext()).isTrue();

        List<Stat> stats = new ArrayList<>();
        builder.elements().get().forEachRemaining(obj -> stats.add((Stat) obj));
        assertThat(stats.stream().map(s -> s.key).collect(Collectors.toList())).contains("count_queries");

        ResponseRegistry.instance = () -> null;
    }


    @Test
    void ensureQueriesCountPartOfStats() throws Exception {

        ResponseRegistry.instance = () -> registry;

        var env = new MockEnvironment();
        var sut = new Statistics(env, ctx);

        var stat = "count_queries";

        assertThat(sut.getStats().stream().filter(s -> stat.equals(s.key)).map(s -> (long) s.value)
            .findFirst().get()).isEqualTo(0L);

        sut.incrementPublishedQueriesCounter();
        sut.incrementPublishedQueriesCounter();
        sut.incrementPublishedQueriesCounter();

        assertThat(sut.getStats().stream().filter(s -> stat.equals(s.key)).map(s -> (long) s.value)
            .findFirst().get()).isEqualTo(3L);

        ResponseRegistry.instance = () -> null;
    }


    @Test
    void ensureResponsesCountPartOfStats() throws Exception {

        ResponseRegistry.instance = () -> registry;

        var env = new MockEnvironment();
        var sut = new Statistics(env, ctx);

        var stat = "count_responses";

        assertThat(sut.getStats().stream().filter(s -> stat.equals(s.key)).map(s -> (long) s.value)
            .findFirst().get()).isEqualTo(0L);

        sut.incrementConsumedResponsesCounter();
        sut.incrementConsumedResponsesCounter();
        sut.incrementConsumedResponsesCounter();

        assertThat(sut.getStats().stream().filter(s -> stat.equals(s.key)).map(s -> (long) s.value)
            .findFirst().get()).isEqualTo(3L);

        ResponseRegistry.instance = () -> null;
    }


    @Test
    void ensureFallbacksCountPartOfStats() throws Exception {

        ResponseRegistry.instance = () -> registry;

        var env = new MockEnvironment();
        var sut = new Statistics(env, ctx);

        var stat = "count_fallbacks";

        assertThat(sut.getStats().stream().filter(s -> stat.equals(s.key)).map(s -> (long) s.value)
            .findFirst().get()).isEqualTo(0L);

        sut.incrementFallbacksCounter();
        sut.incrementFallbacksCounter();
        sut.incrementFallbacksCounter();

        assertThat(sut.getStats().stream().filter(s -> stat.equals(s.key)).map(s -> (long) s.value)
            .findFirst().get()).isEqualTo(3L);

        ResponseRegistry.instance = () -> null;
    }


    @Test
    void ensureMeasuresAndRetainsLatencyStatisticsInBoundedCollection() throws Exception {

        var env = new MockEnvironment();
        var sut = new Statistics(env, ctx);

        // NOOP
        sut.measureLatency(null, 43L);

        assertThat(sut.getMinLatency()).isEqualTo(0);
        assertThat(sut.getMaxLatency()).isEqualTo(0);
        assertThat(sut.getAvgLatency()).isEqualTo(0.0, within(1.0));

        for (long i = 1; i < 1000; i++) {
            // NOOP
            sut.measureLatency(1L, 1L);
        }

        assertThat(sut.getMinLatency()).isEqualTo(0);
        assertThat(sut.getMaxLatency()).isEqualTo(0);
        assertThat(sut.getAvgLatency()).isEqualTo(0.0, within(1.0));

        // Given that we have measured 1000 20ms latencies
        for (long i = 1; i < 1000; i++) {
            sut.measureLatency(i, i + 20);
        }

        assertThat(sut.getMinLatency()).isEqualTo(20);
        assertThat(sut.getMaxLatency()).isEqualTo(20);
        assertThat(sut.getAvgLatency()).isEqualTo(20.0, within(1.0));

        // If we measure one 100ms latencies
        sut.measureLatency(1L, 101L);

        // Retains previous min/avg
        assertThat(sut.getMinLatency()).isEqualTo(20);
        assertThat(sut.getMaxLatency()).isEqualTo(100);
        assertThat(sut.getAvgLatency()).isEqualTo(20.0, within(1.0));

        // If we measure 1000 100ms latencies
        for (long i = 1; i < 1000; i++) {
            sut.measureLatency(i, i + 100);
        }

        // Filled
        assertThat(sut.getMinLatency()).isEqualTo(100);
        assertThat(sut.getMaxLatency()).isEqualTo(100);
        assertThat(sut.getAvgLatency()).isEqualTo(100.0, within(1.0));
    }


    @Test
    void ensureCanHaveStatWithOrWithoutTimestamp() throws Exception {

        Stat s1 = Statistics.Stat.of("foo", 123);
        Stat s2 = Statistics.Stat.at("bar", 42);

        assertThat(s1.timestamp).isNull();
        assertThat(s2.timestamp).isNotNull();
    }
}
