package com.studiomediatech.queries;

import com.studiomediatech.Query;
import com.studiomediatech.Response;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.concurrent.atomic.AtomicReference;


public class Querent<T> implements MessageListener {

    private final Query<T> query;
    private final AtomicReference<Response<T>> response;

    public Querent(Query<T> query, Response<T> orDefault) {

        this.query = query;
        this.response = new AtomicReference<Response<T>>(orDefault);
    }

    @Override
    public void onMessage(Message message) {

        // TODO Auto-generated method stub
    }


    public Response<T> publish(RabbitTemplate rabbit) {

        // TODO Auto-generated method stub
        return response.get();
    }
}
