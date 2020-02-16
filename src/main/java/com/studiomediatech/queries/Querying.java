package com.studiomediatech.queries;

import com.studiomediatech.Query;
import com.studiomediatech.Response;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.concurrent.atomic.AtomicReference;


public class Querying<T> implements MessageListener {

    private final Query<T> query;
    private final AtomicReference<Response<T>> response;

    public Querying(Query<T> query, Response<T> orDefault) {

        this.query = query;
        this.response = new AtomicReference<Response<T>>(orDefault);
    }

    @Override
    public void onMessage(Message message) {

        System.out.println("|<------------------ RECEIVED RESPONSE: " + message);
    }


    public Response<T> publish(RabbitTemplate rabbit, String queue, Runnable onDone) {

        try {
            rabbit.send("queries", query.getTerm(),
                MessageBuilder.withBody("{}".getBytes()).setReplyTo(queue).build());
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(query.getWaitingFor().toMillis());
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        onDone.run();

        // TODO Auto-generated method stub
        return response.get();
    }
}
