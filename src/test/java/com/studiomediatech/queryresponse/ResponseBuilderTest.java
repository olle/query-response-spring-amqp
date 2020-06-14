package com.studiomediatech.queryresponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class ResponseBuilderTest {

    @Mock
    ResponseRegistry registry;

    @InjectMocks
    ResponseBuilder sut;

    @Test
    void ensureCreatesLegacyBuilderByProxy() {

        ChainingResponseBuilder<String> builder = sut.respondTo("foobar", String.class);
        assertThat(builder).isNotNull();
    }


    @Test
    void ensureUsesInjectedRegistry() {

        ChainingResponseBuilder<String> builder = sut.respondTo("foobar", String.class);
        builder.from("foo", "bar", "baz");

        verify(registry).accept(builder);
    }
}
