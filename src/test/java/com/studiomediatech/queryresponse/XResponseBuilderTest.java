package com.studiomediatech.queryresponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class XResponseBuilderTest {

    @Mock
    ResponseRegistry registry;

    @InjectMocks
    XResponseBuilder sut;

    @Test
    void ensureCreatesLegacyBuilderByProxy() {

        YResponseBuilder<String> builder = sut.respondTo("foobar", String.class);
        assertThat(builder).isNotNull();
    }


    @Test
    void ensureUsesInjectedRegistry() {

        YResponseBuilder<String> builder = sut.respondTo("foobar", String.class);
        builder.from("foo", "bar", "baz");

        verify(registry).accept(builder);
    }
}
