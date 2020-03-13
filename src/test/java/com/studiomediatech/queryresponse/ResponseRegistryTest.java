package com.studiomediatech.queryresponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.Mockito;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ResponseRegistryTest {

    @Mock
    RabbitFacade facade;

    @Test
    void ensureInstanceIsInjectedByContextLifecycleMethod() {

        ResponseRegistry.instance = () -> null;

        var sut = new ResponseRegistry(null);
        var ctx = Mockito.mock(ApplicationContext.class);
        when(ctx.getBean(ResponseRegistry.class)).thenReturn(sut);

        sut.setApplicationContext(ctx);

        assertThat(ResponseRegistry.instance.get()).isEqualTo(sut);

        ResponseRegistry.instance = () -> null;
    }


    @Test
    void ensureThrowsOnMissingBean() throws Exception {

        assertThrows(IllegalStateException.class,
            () -> {
                ResponseRegistry.instance = () -> null;
                ResponseRegistry.register(new Responses<>("foobar"));
            });
    }


    @SuppressWarnings("static-access")
    @Test
    void ensureInstanceIsInvokedOnRegister() throws Exception {

        var mock = Mockito.mock(ResponseRegistry.class);
        ResponseRegistry.instance = () -> mock;

        Responses<Object> responses = new Responses<>("foobar");
        new ResponseRegistry(null).register(responses);

        verify(mock).accept(responses);

        ResponseRegistry.instance = () -> null;
    }


    @Test
    void ensureAcceptResponses() throws Exception {

        new ResponseRegistry(facade).accept(new Responses<>("some-term"));

        verify(facade).declareQueue(Mockito.isA(Response.class));
        verify(facade).declareBinding(Mockito.isA(Response.class));
        verify(facade).addListener(Mockito.isA(Response.class));
    }
}
