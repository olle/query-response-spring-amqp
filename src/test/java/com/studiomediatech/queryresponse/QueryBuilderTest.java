package com.studiomediatech.queryresponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class QueryBuilderTest {

    @Mock
    QueryRegistry registry;

    @InjectMocks
    QueryBuilder sut;

    @Test
    void ensureCreatesLegacyBuilderByProxy() {

        YQueryBuilder<String> builder = sut.queryFor("foobar", String.class);
        assertThat(builder).isNotNull();
    }


    @Test
    void ensureUsesInjectedRegistry() {

        YQueryBuilder<String> builder = sut.queryFor("foobar", String.class);
        builder.waitingFor(1).orEmpty();

        verify(registry).accept(builder);
    }
}
