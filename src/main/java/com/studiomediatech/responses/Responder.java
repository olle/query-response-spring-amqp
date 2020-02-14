package com.studiomediatech.responses;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;


public class Responder implements MessageListener {

    private final String actualName;

    public Responder(Queue queue) {

        actualName = queue.getActualName();
    }

    @Override
    public void onMessage(Message message) {

        System.out.println(">>>>>>>>>>>>>>>< RECEIVED MESSAGE: " + message);
    }
}
