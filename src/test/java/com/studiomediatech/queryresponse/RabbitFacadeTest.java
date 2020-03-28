package com.studiomediatech.queryresponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class RabbitFacadeTest {

    @Mock
    RabbitAdmin admin;
    @Mock
    RabbitTemplate template;
    @Mock
    ConnectionFactory connectionFactory;

    @Captor
    ArgumentCaptor<Queue> queue;
    @Captor
    ArgumentCaptor<Binding> binding;

    @Test
    void ensureDeclaresQueueForQuery() {

        var sut = new RabbitFacade(admin, template, connectionFactory, new TopicExchange("queries"));

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

        var sut = new RabbitFacade(admin, template, connectionFactory, new TopicExchange("queries"));

        var query = new Query<>();
        var listenerContainer = sut.createMessageListenerContainer(query);
        assertThat(listenerContainer.getQueueNames()).contains(query.getQueueName());
        assertThat(listenerContainer.getMessageListener()).isSameAs(query);
    }


    @Test
    void ensureRemovesListenerForQuery() {

        var sut = new RabbitFacade(admin, template, connectionFactory, new TopicExchange("queries"));

        var query = new Query<>();
        sut.removeQueue(query);

        verify(admin).deleteQueue(query.getQueueName());
    }


    @Test
    void ensureDeclaresQueueForResponse() {

        var sut = new RabbitFacade(admin, template, connectionFactory, new TopicExchange("queries"));

        sut.declareQueue(new Response<>(new ResponseBuilder<>("some-term")));

        verify(admin).declareQueue(queue.capture());

        assertThat(queue.getValue().isDurable()).isFalse();
        assertThat(queue.getValue().isExclusive()).isTrue();
        assertThat(queue.getValue().isAutoDelete()).isTrue();
    }


    @Test
    void ensureDeclaresBindingForResponse() {

        var sut = new RabbitFacade(admin, template, connectionFactory, new TopicExchange("queries"));

        var response = new Response<>(new ResponseBuilder<>("some-term"));
        sut.declareBinding(response);

        verify(admin).declareBinding(binding.capture());

        assertThat(binding.getValue().isDestinationQueue()).isTrue();
        assertThat(binding.getValue().getExchange()).isEqualTo("queries");
        assertThat(binding.getValue().getDestination()).isEqualTo(response.getQueueName());
    }


    @Test
    void ensureAddsListenerForResponse() {

        var sut = new RabbitFacade(admin, template, connectionFactory, new TopicExchange("queries"));

        var response = new Response<>(new ResponseBuilder<>("some-term"));
        var listenerContainer = sut.createMessageListenerContainer(response);
        assertThat(listenerContainer.getQueueNames()).contains(response.getQueueName());
        assertThat(listenerContainer.getMessageListener()).isSameAs(response);
    }
}
