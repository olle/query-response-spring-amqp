package com.studiomediatech.queryresponse;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.concurrent.atomic.AtomicReference;


class Querying<T> implements MessageListener {

    private final Queries<T> queries;
    private final AtomicReference<Results<T>> response;

    public Querying(Queries<T> queries) {

        this.queries = queries;
        this.response = new AtomicReference<>(Results.empty());
    }

    @Override
    public void onMessage(Message message) {

        System.out.println("|<------------------ RECEIVED RESPONSE: " + message);
    }


    public Results<T> publish(RabbitTemplate rabbit, String queue, Runnable onDone) {

        try {
            rabbit.send("queries", queries.getQueryForTerm(),
                MessageBuilder.withBody("{}".getBytes()).setReplyTo(queue).build());
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(queries.getWaitingFor().toMillis());
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        onDone.run();

        // TODO Auto-generated method stub
        return response.get();
    }
}
