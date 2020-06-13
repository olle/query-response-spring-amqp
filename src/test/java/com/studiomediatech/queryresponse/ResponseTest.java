package com.studiomediatech.queryresponse;

import org.json.JSONException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import org.skyscreamer.jsonassert.JSONAssert;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class ResponseTest {

    @Mock
    RabbitFacade facade;
    @Mock
    Statistics stats;

    @Captor
    ArgumentCaptor<Message> message;

    @Test
    @DisplayName("after consuming a query message, a response is published")
    void ensurePublishesResponseOnConsumedQueryMessage() throws JSONException {

        AtomicReference<YResponseBuilder<String>> capture = new AtomicReference<>(null);

        YResponseBuilder.<String>respondTo("some-query", String.class)
            .withSink(capture::set)
            .withAll()
            .from("foo", "bar", "baz");

        var sut = Response.from(capture.get());
        sut.accept(facade, stats);

        var query = MessageBuilder.withBody("{}".getBytes()).setReplyTo("reply-to").build();
        sut.onMessage(query);

        verify(facade).publishResponse(eq(""), eq("reply-to"), message.capture());

        Message m = message.getValue();
        assertThat(m).isNotNull();

        JSONAssert.assertEquals(new String(m.getBody()), "{elements: ['foo', 'bar', 'baz']}", true);
    }


    @Test
    @DisplayName("response is built from iterator after a query message is consumed")
    void ensureCallsElementsIteratorAfterQueryConsumed() throws Exception {

        AtomicReference<YResponseBuilder<String>> capture = new AtomicReference<>(null);

        YResponseBuilder.<String>respondTo("some-query", String.class)
            .withSink(capture::set)
            .withAll()
            .from(() -> List.of("foo", "bar").iterator());

        var sut = Response.from(capture.get());
        sut.accept(facade, stats);

        var query = MessageBuilder.withBody("{}".getBytes()).setReplyTo("reply-to").build();
        sut.onMessage(query);

        verify(facade).publishResponse(eq(""), eq("reply-to"), message.capture());

        Message m = message.getValue();
        assertThat(m).isNotNull();

        JSONAssert.assertEquals(new String(m.getBody()), "{elements: ['foo', 'bar']}", true);
    }


    @Test
    @DisplayName("responses are published as batches of a given size, plus leftover")
    void ensurePublishedBatchResponses() throws Exception {

        AtomicReference<YResponseBuilder<String>> capture = new AtomicReference<>(null);

        YResponseBuilder.<String>respondTo("some-query", String.class)
            .withSink(capture::set)
            .withBatchesOf(2)
            .from("foo", "bar", "baz", "goo", "gar");

        var sut = Response.from(capture.get());
        sut.accept(facade, stats);

        sut.onMessage(MessageBuilder.withBody("{}".getBytes()).setReplyTo("reply-to").build());

        verify(facade, atLeast(3)).publishResponse(eq(""), eq("reply-to"), message.capture());

        List<Message> ms = message.getAllValues();
        assertThat(ms).hasSize(3);

        String json1 = new String(ms.get(0).getBody());
        String json2 = new String(ms.get(1).getBody());
        String json3 = new String(ms.get(2).getBody());

        JSONAssert.assertEquals(json1, "{elements: ['foo', 'bar']}", true);
        JSONAssert.assertEquals(json2, "{elements: ['baz', 'goo']}", true);
        JSONAssert.assertEquals(json3, "{elements: ['gar']}", true);
    }
}
