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
    void ensureThrowsOnMissingBean() throws Exception {

        assertThrows(IllegalStateException.class,
            () -> {
                QueryRegistry.instance = () -> null;
                QueryRegistry.register(new QueryBuilder<>("foobar", String.class));
            });
    }


    @SuppressWarnings("static-access")
    @Test
    void ensureInstanceIsInvokedOnRegister() throws Exception {

        var mock = Mockito.mock(QueryRegistry.class);
        QueryRegistry.instance = () -> mock;

        var queryBuilder = new QueryBuilder<>("foobar", String.class);
        new QueryRegistry(null).register(queryBuilder);

        verify(mock).accept(queryBuilder);

        QueryRegistry.instance = () -> null;
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
