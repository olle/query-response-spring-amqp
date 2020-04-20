package com.studiomediatech.queryresponse;

import com.studiomediatech.queryresponse.Statistics.Stat;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

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
    void ensureBuildsResponseOnInitialization() {

        ResponseRegistry.instance = () -> registry;

        var env = new MockEnvironment();
        var meters = new SimpleMeterRegistry();

        new Statistics(env, ctx, meters);

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
        var meters = new SimpleMeterRegistry();
        var sut = new Statistics(env, ctx, meters);

        var stat = "count_queries";

        assertThat(sut.getStats().stream().filter(s -> stat.equals(s.key)).map(s -> (long) s.value)
            .findFirst().get()).isEqualTo(0L);

        sut.incrementQueriesCounter();
        sut.incrementQueriesCounter();
        sut.incrementQueriesCounter();

        assertThat(sut.getStats().stream().filter(s -> stat.equals(s.key)).map(s -> (long) s.value)
            .findFirst().get()).isEqualTo(3L);

        ResponseRegistry.instance = () -> null;
    }


    @Test
    void ensureResponsesCountPartOfStats() throws Exception {

        ResponseRegistry.instance = () -> registry;

        var env = new MockEnvironment();
        var meters = new SimpleMeterRegistry();
        var sut = new Statistics(env, ctx, meters);

        var stat = "count_responses";

        assertThat(sut.getStats().stream().filter(s -> stat.equals(s.key)).map(s -> (long) s.value)
            .findFirst().get()).isEqualTo(0L);

        sut.incrementResponsesCounter();
        sut.incrementResponsesCounter();
        sut.incrementResponsesCounter();

        assertThat(sut.getStats().stream().filter(s -> stat.equals(s.key)).map(s -> (long) s.value)
            .findFirst().get()).isEqualTo(3L);

        ResponseRegistry.instance = () -> null;
    }


    @Test
    void ensureFallbacksCountPartOfStats() throws Exception {

        ResponseRegistry.instance = () -> registry;

        var env = new MockEnvironment();
        var meters = new SimpleMeterRegistry();
        var sut = new Statistics(env, ctx, meters);

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
}
