package com.studiomediatech.queryresponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.Mockito;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.amqp.core.Message;

import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class QueryRegistryTest {

    @Mock
    RabbitFacade facade;
    @Mock
    Statistics stats;

    @Test
    void ensureThrowsOnMissingBean() {

        assertThrows(IllegalStateException.class,
            () -> {
                QueryRegistry.instance = () -> null;
                QueryRegistry.register(new QueryBuilder<>("foobar", String.class));
            });
    }


    @SuppressWarnings("static-access")
    @Test
    void ensureInstanceIsInvokedOnRegister() {

        var mock = Mockito.mock(QueryRegistry.class);
        QueryRegistry.instance = () -> mock;

        var queryBuilder = new QueryBuilder<>("foobar", String.class);
        new QueryRegistry(null, null).register(queryBuilder);

        verify(mock).accept(queryBuilder);

        QueryRegistry.instance = () -> null;
    }


    @Test
    void ensureAcceptQueries() {

        var queries = new QueryBuilder<>("foobar", String.class);
        queries.waitingFor(123);

        new QueryRegistry(facade, stats).accept(queries);

        verify(facade).declareQueue(Mockito.isA(Query.class));
        verify(facade).addListener(Mockito.isA(Query.class));
        verify(facade).publishQuery(Mockito.eq("foobar"), Mockito.isA(Message.class));

        verify(stats).incrementPublishedQueriesCounter();
        verify(stats).incrementConsumedResponsesCounter();
    }
}
