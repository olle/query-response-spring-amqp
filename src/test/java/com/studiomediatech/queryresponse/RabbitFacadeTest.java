package com.studiomediatech.queryresponse;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;

import org.springframework.context.support.GenericApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RabbitFacadeTest {

    @Mock
    RabbitAdmin admin;
    @Mock
    RabbitTemplate template;
    @Mock
    ConnectionFactory connectionFactory;
    @Mock
    GenericApplicationContext ctx;

    @Captor
    ArgumentCaptor<Queue> queue;
    @Captor
    ArgumentCaptor<Binding> binding;
    @Captor
    ArgumentCaptor<Message> message;

    @Test
    @Disabled
    void ensureDeclaresQueueForQuery() {

        RabbitFacade sut = new RabbitFacade(admin, template, connectionFactory, new TopicExchange("queries"), ctx);

        Query<?> query = new Query<>();
        sut.declareQueue(query);

        verify(admin).declareQueue(queue.capture());

        Queue q = queue.getValue();

        assertThat(q.getActualName()).isEqualTo(query.getQueueName());
        assertThat(q.isDurable()).isFalse();
        assertThat(q.isAutoDelete()).isTrue();
        assertThat(q.isExclusive()).isTrue();
        assertThat(q.getArguments().get("x-queue-master-locator")).isEqualTo("client-local");
    }

    @Test
    @Disabled
    void ensureDeclaresQueueForResponse() {

        RabbitFacade sut = new RabbitFacade(admin, template, connectionFactory, new TopicExchange("queries"), ctx);

        Response<Object> response = new Response<>("some-routing-key");
        sut.declareQueue(response);

        verify(admin).declareQueue(queue.capture());

        assertThat(queue.getValue().getActualName()).isEqualTo(response.getQueueName());
        assertThat(queue.getValue().isDurable()).isFalse();
        assertThat(queue.getValue().isAutoDelete()).isTrue();
        assertThat(queue.getValue().isExclusive()).isTrue();
        assertThat(queue.getValue().getArguments().get("x-queue-master-locator")).isEqualTo("client-local");
    }

    @Test
    void ensureAddsListenerForQuery() {

        RabbitFacade sut = new RabbitFacade(admin, template, connectionFactory, new TopicExchange("queries"), ctx);

        Query<?> query = new Query<>();
        sut.addListener(query);

        String queueName = query.getQueueName();
        assertThat(sut.containers.containsKey(queueName));

        DirectMessageListenerContainer listenerContainer = sut.containers.get(queueName);
        assertThat(listenerContainer.getQueueNames()).contains(queueName);
        assertThat(listenerContainer.getMessageListener()).isSameAs(query);
    }

    @Test
    void ensureRemovesQueueForQuery() throws Exception {

        RabbitFacade sut = new RabbitFacade(admin, template, connectionFactory, new TopicExchange("queries"), ctx);

        Query<?> query = new Query<>();
        sut.removeQueue(query);

        verify(admin).deleteQueue(query.getQueueName());
    }

    @Test
    void ensureRemovesQueueForResponse() throws Exception {

        RabbitFacade sut = new RabbitFacade(admin, template, connectionFactory, new TopicExchange("queries"), ctx);

        Response<?> response = new Response<>("foobaar");
        sut.removeQueue(response);

        verify(admin).deleteQueue(response.getQueueName());
    }

    @Test
    void ensureRemovesListenerForQuery() {

        RabbitFacade sut = new RabbitFacade(admin, template, connectionFactory, new TopicExchange("queries"), ctx);

        Query<?> query = new Query<>();
        sut.addListener(query);

        String queueName = query.getQueueName();
        assertThat(sut.containers.containsKey(queueName));

        sut.removeListener(query);
        sut.removeListener(query); // Idempotent!

        assertThat(!sut.containers.containsKey(queueName));
    }

    @Test
    void ensureRemovesListenerForResponse() {

        RabbitFacade sut = new RabbitFacade(admin, template, connectionFactory, new TopicExchange("queries"), ctx);

        Response<?> response = new Response<>("bar");
        sut.addListener(response);

        String queueName = response.getQueueName();
        assertThat(sut.containers.containsKey(queueName));

        sut.removeListener(response);
        sut.removeListener(response); // Idempotent!

        assertThat(!sut.containers.containsKey(queueName));
    }

    @Test
    void ensureDeclaresBindingForResponse() {

        RabbitFacade sut = new RabbitFacade(admin, template, connectionFactory, new TopicExchange("queries"), ctx);

        Response<?> response = new Response<>("some-term");
        sut.declareBinding(response);

        verify(admin).declareBinding(binding.capture());

        assertThat(binding.getValue().isDestinationQueue()).isTrue();
        assertThat(binding.getValue().getExchange()).isEqualTo("queries");
        assertThat(binding.getValue().getDestination()).isEqualTo(response.getQueueName());
    }

    @Test
    void ensureAddsListenerForResponse() {

        RabbitFacade sut = new RabbitFacade(admin, template, connectionFactory, new TopicExchange("queries"), ctx);

        Response<?> response = new Response<>("some-term");
        sut.addListener(response);

        String queueName = response.getQueueName();
        assertThat(sut.containers.containsKey(queueName));

        DirectMessageListenerContainer listenerContainer = sut.containers.get(queueName);
        assertThat(listenerContainer.getQueueNames()).contains(queueName);
        assertThat(listenerContainer.getMessageListener()).isSameAs(response);
    }

    @Test
    void ensurePublishesQuery() throws Exception {

        RabbitFacade sut = new RabbitFacade(admin, template, connectionFactory, new TopicExchange("queries"), ctx);

        byte[] body = "{}".getBytes();
        sut.publishQuery("some-routing-key", MessageBuilder.withBody(body).build());

        verify(template).send(Mockito.eq("queries"), Mockito.eq("some-routing-key"), message.capture());

        assertThat(message.getValue().getMessageProperties().getDeliveryMode())
                .isEqualTo(MessageDeliveryMode.NON_PERSISTENT);
        assertThat(message.getValue().getBody()).isEqualTo(body);
    }

    @Test
    void ensurePublishesResponse() throws Exception {

        RabbitFacade sut = new RabbitFacade(admin, template, connectionFactory, new TopicExchange("queries"), ctx);

        byte[] body = "{}".getBytes();
        sut.publishResponse("some-exchange", "some-routing-key", MessageBuilder.withBody(body).build());

        verify(template).send(Mockito.eq("some-exchange"), Mockito.eq("some-routing-key"), message.capture());

        assertThat(message.getValue().getMessageProperties().getDeliveryMode())
                .isEqualTo(MessageDeliveryMode.NON_PERSISTENT);
        assertThat(message.getValue().getBody()).isEqualTo(body);
    }

    @Test
    void ensureFailureToPublishResponseIsNotThrown() throws Exception {

        RabbitFacade sut = new RabbitFacade(admin, template, connectionFactory, new TopicExchange("queries"), ctx);

        Mockito.doThrow(RuntimeException.class).when(template).send(Mockito.anyString(), Mockito.anyString(),
                Mockito.any(Message.class));

        try {
            sut.publishResponse("some-exchange", "some-routing-key", MessageBuilder.withBody("{}".getBytes()).build());
        } catch (Exception e) {
            fail("Should not throw");
        }

        verify(template).send(Mockito.eq("some-exchange"), Mockito.eq("some-routing-key"), message.capture());
    }
}
