package com.studiomediatech.queryresponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.Mockito;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class QueryRegistryTest {

    @Mock
    RabbitFacade facade;

    @Test
    void ensureThrowsOnMissingRegistryBean() throws Exception {

        assertThrows(IllegalStateException.class,
            () -> {
                QueryRegistry.instance = () -> null;
                QueryRegistry.register(new QueryBuilder<>("foobar", String.class));
            });
    }


    @Test
    void ensureAcceptResponses() throws Exception {

        var queries = new QueryBuilder<>("foobar", String.class);
        queries.waitingFor(123);

        new QueryRegistry(facade).accept(queries);

        verify(facade).declareQueue(Mockito.isA(Query.class));
        verify(facade).addListener(Mockito.isA(Query.class));
    }
}
