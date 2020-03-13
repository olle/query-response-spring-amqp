package com.studiomediatech.queryresponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class RabbitFacadeTest {

    @Mock
    RabbitAdmin admin;
    @Mock
    RabbitTemplate template;
    @Mock
    DirectMessageListenerContainer listener;

    @Captor
    ArgumentCaptor<Queue> queue;
    @Captor
    ArgumentCaptor<String> queueName;
    @Captor
    ArgumentCaptor<Binding> binding;

    @Test
    void ensureDeclaresQueueForQuery() {

        var sut = new RabbitFacade(admin, template, listener);

        var query = new Query<>();
        sut.declareQueue(query);

        verify(admin).declareQueue(queue.capture());

        assertThat(queue.getValue().getActualName()).isEqualTo(query.getQueueName());
        assertThat(queue.getValue().isDurable()).isFalse();
        assertThat(queue.getValue().isExclusive()).isTrue();
        assertThat(queue.getValue().isAutoDelete()).isTrue();
    }


    @Test
    void ensureAddsListenerForQuery() {

        var sut = new RabbitFacade(admin, template, listener);

        var query = new Query<>();
        sut.addListener(query);

        verify(listener).addQueueNames(queueName.capture());
        assertThat(queueName.getValue()).isEqualTo(query.getQueueName());

        verify(listener).setMessageListener(query);
    }


    @Test
    void ensureRemovesListenerForQuery() throws Exception {

        var sut = new RabbitFacade(admin, template, listener);

        var query = new Query<>();
        sut.removeListener(query);

        verify(listener).removeQueueNames(query.getQueueName());
        verify(admin).deleteQueue(query.getQueueName());
    }


    @Test
    void ensureDeclaresQueueForResponse() {

        var sut = new RabbitFacade(admin, template, listener);

        sut.declareQueue(new Response<>(new Responses<>("some-term")));

        verify(admin).declareQueue(queue.capture());

        assertThat(queue.getValue().isDurable()).isFalse();
        assertThat(queue.getValue().isExclusive()).isTrue();
        assertThat(queue.getValue().isAutoDelete()).isTrue();
    }


    @Test
    void ensureDeclaresBindingForResponse() throws Exception {

        var sut = new RabbitFacade(admin, template, listener);

        var response = new Response<>(new Responses<>("some-term"));
        sut.declareBinding(response);

        verify(admin).declareBinding(binding.capture());

        assertThat(binding.getValue().isDestinationQueue()).isTrue();
        assertThat(binding.getValue().getExchange()).isEqualTo("queries");
        assertThat(binding.getValue().getDestination()).isEqualTo(response.getQueueName());
    }


    @Test
    void ensureAddsListenerForResponse() {

        var sut = new RabbitFacade(admin, template, listener);

        var response = new Response<>(new Responses<>("some-term"));
        sut.addListener(response);

        verify(listener).addQueueNames(queueName.capture());
        assertThat(queueName.getValue()).isEqualTo(response.getQueueName());

        verify(listener).setMessageListener(response);
    }
}
