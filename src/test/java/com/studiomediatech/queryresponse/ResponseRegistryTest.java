package com.studiomediatech.queryresponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.Mockito;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.context.ApplicationContext;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ResponseRegistryTest {

    @Mock
    RabbitFacade facade;
    @Mock
    Statistics stats;

    @Test
    void ensureInstanceIsInjectedByContextLifecycleMethod() {

        ResponseRegistry.instance = () -> null;

        var sut = new ResponseRegistry(null, null);
        var ctx = Mockito.mock(ApplicationContext.class);
        when(ctx.getBean(ResponseRegistry.class)).thenReturn(sut);

        sut.setApplicationContext(ctx);

        assertThat(ResponseRegistry.instance.get()).isEqualTo(sut);

        ResponseRegistry.instance = () -> null;
    }


    @Test
    void ensureThrowsOnMissingBean() {

        AtomicReference<ResponseBuilder<String>> capture = new AtomicReference<>(null);

        ResponseBuilder.<String>respondTo("some-query", String.class)
            .withSink(capture::set)
            .withAll()
            .from("foo", "bar", "baz");

        assertThrows(IllegalStateException.class,
            () -> {
                ResponseRegistry.instance = () -> null;
                ResponseRegistry.register(capture.get());
            });
    }


    @SuppressWarnings("static-access")
    @Test
    void ensureInstanceIsInvokedOnRegister() {

        AtomicReference<ResponseBuilder<String>> capture = new AtomicReference<>(null);

        ResponseBuilder.<String>respondTo("some-query", String.class)
            .withSink(capture::set)
            .withAll()
            .from("foo", "bar", "baz");

        var mock = Mockito.mock(ResponseRegistry.class);
        ResponseRegistry.instance = () -> mock;

        new ResponseRegistry(null, stats).register(capture.get());

        verify(mock).accept(capture.get());

        ResponseRegistry.instance = () -> null;
    }


    @Test
    void ensureAcceptResponses() {

        AtomicReference<ResponseBuilder<String>> capture = new AtomicReference<>(null);

        ResponseBuilder.<String>respondTo("some-query", String.class)
            .withSink(capture::set)
            .withAll()
            .from("foo", "bar", "baz");

        new ResponseRegistry(facade, stats).accept(capture.get());

        verify(facade).declareQueue(Mockito.isA(Response.class));
        verify(facade).declareBinding(Mockito.isA(Response.class));
        verify(facade).addListener(Mockito.isA(Response.class));
    }


    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    void ensureCallsToRemoveListenerAndQueueOnFailure() throws Exception {

        ResponseRegistry sut = new ResponseRegistry(facade, stats);

        Response response = Mockito.mock(Response.class);
        Mockito.doThrow(new RuntimeException()).when(response).accept(Mockito.any(), Mockito.any());

        sut.doAccept(response);

        verify(facade).removeListener(response);
        verify(facade).removeQueue(response);
    }
}
