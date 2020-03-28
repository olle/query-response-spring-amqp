package com.studiomediatech.queryresponse;

import org.json.JSONException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.mockito.junit.jupiter.MockitoExtension;

import org.skyscreamer.jsonassert.JSONAssert;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;

import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ResponseTest {

    @Mock
    RabbitFacade facade;

    @Captor
    ArgumentCaptor<Message> message;

    @Test
    @DisplayName("after consuming a query message, a response is published")
    void ensurePublishesResponseOnConsumedQueryMessage() throws JSONException {

        var sut = Response.valueOf(new ResponseBuilder<>("query-term", List.of("foo", "bar", "baz")));
        sut.accept(facade);

        var query = MessageBuilder.withBody("{}".getBytes()).setReplyTo("reply-to").build();
        sut.onMessage(query);

        verify(facade).publishResponse(eq(""), eq("reply-to"), message.capture());

        Message m = message.getValue();
        assertThat(m).isNotNull();

        JSONAssert.assertEquals(new String(m.getBody()),
            "{"
            + "count: 3,"
            + "total: 3,"
            + "elements: ['foo', 'bar', 'baz']"
            + "}", true);
    }


    @Test
    @DisplayName("response is built from iterator after a query message is consumed")
    void ensureCallsElementsIteratorAfterQueryConsumed() throws Exception {

        var it = Mockito.mock(Iterator.class);
        when(it.hasNext()).thenReturn(true, true, false);
        when(it.next()).thenReturn("foo", "bar");

        @SuppressWarnings("unchecked")
        var sut = Response.valueOf(new ResponseBuilder<>("foo", it, () -> 42));
        sut.accept(facade);

        verifyNoInteractions(it);

        var query = MessageBuilder.withBody("{}".getBytes()).setReplyTo("reply-to").build();
        sut.onMessage(query);

        verify(it, atLeast(3)).hasNext();
        verify(it, atLeast(2)).next();

        verify(facade).publishResponse(eq(""), eq("reply-to"), message.capture());

        Message m = message.getValue();
        assertThat(m).isNotNull();

        JSONAssert.assertEquals(new String(m.getBody()),
            "{"
            + "count: 2,"
            + "total: 42,"
            + "elements: ['foo', 'bar']"
            + "}", true);
    }
}
