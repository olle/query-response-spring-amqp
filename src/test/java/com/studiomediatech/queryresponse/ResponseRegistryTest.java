package com.studiomediatech.queryresponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;


@ExtendWith(MockitoExtension.class)
class ResponseRegistryTest {

    @Mock
    RabbitAdmin rabbitAdmin;
    @Mock
    RabbitTemplate rabbitTemplate;
    @Mock
    DirectMessageListenerContainer listener;

    @Test
    void ensureInstanceIsInjected() {

        var sut = new ResponseRegistry(rabbitAdmin, listener, rabbitTemplate);
    }
}
