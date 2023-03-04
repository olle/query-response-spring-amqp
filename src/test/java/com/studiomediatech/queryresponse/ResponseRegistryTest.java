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

    QueryResponseConfigurationProperties props = new QueryResponseConfigurationProperties();

    @Test
    void ensureInstanceIsInjectedByContextLifecycleMethod() {

        ResponseRegistry.instance = () -> null;

        ResponseRegistry sut = new ResponseRegistry(null, null, props);
        ApplicationContext ctx = Mockito.mock(ApplicationContext.class);
        when(ctx.getBean(ResponseRegistry.class)).thenReturn(sut);

        sut.setApplicationContext(ctx);

        assertThat(ResponseRegistry.instance.get()).isEqualTo(sut);

        ResponseRegistry.instance = () -> null;
    }

    @Test
    void ensureThrowsOnMissingBean() {

        AtomicReference<ChainingResponseBuilder<String>> capture = new AtomicReference<>(null);

        ChainingResponseBuilder.<String> respondTo("some-query", String.class).withSink(capture::set).withAll()
                .from("foo", "bar", "baz");

        assertThrows(IllegalStateException.class, () -> {
            ResponseRegistry.instance = () -> null;
            ResponseRegistry.register(capture.get());
        });
    }

    @SuppressWarnings("static-access")
    @Test
    void ensureInstanceIsInvokedOnRegister() {

        AtomicReference<ChainingResponseBuilder<String>> capture = new AtomicReference<>(null);

        ChainingResponseBuilder.<String> respondTo("some-query", String.class).withSink(capture::set).withAll()
                .from("foo", "bar", "baz");

        ResponseRegistry mock = Mockito.mock(ResponseRegistry.class);
        ResponseRegistry.instance = () -> mock;

        new ResponseRegistry(null, stats, props).register(capture.get());

        verify(mock).accept(capture.get());

        ResponseRegistry.instance = () -> null;
    }

    @Test
    void ensureAcceptResponses() {

        AtomicReference<ChainingResponseBuilder<String>> capture = new AtomicReference<>(null);

        ChainingResponseBuilder.<String> respondTo("some-query", String.class).withSink(capture::set).withAll()
                .from("foo", "bar", "baz");

        new ResponseRegistry(facade, stats, props).accept(capture.get());

        verify(facade).declareQueue(Mockito.isA(Response.class));
        verify(facade).declareBinding(Mockito.isA(Response.class));
        verify(facade).addListener(Mockito.isA(Response.class));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    void ensureCallsToRemoveListenerAndQueueOnFailure() throws Exception {

        ResponseRegistry sut = new ResponseRegistry(facade, stats, props);

        Response response = Mockito.mock(Response.class);
        Mockito.doThrow(new RuntimeException()).when(response).accept(Mockito.any(), Mockito.any());

        sut.doAccept(response);

        verify(facade).removeListener(response);
        verify(facade).removeQueue(response);
    }
}
