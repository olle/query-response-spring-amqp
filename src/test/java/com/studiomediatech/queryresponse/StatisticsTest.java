package com.studiomediatech.queryresponse;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.context.ApplicationContext;

import org.springframework.mock.env.MockEnvironment;

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

        ResponseRegistry.instance = () -> null;
    }
}
