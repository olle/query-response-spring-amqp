package com.studiomediatech.queryresponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;


class Querying<T> implements MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(Querying.class);

    private final Queries<T> queries;
    private final AtomicReference<Results<T>> response;

    public Querying(Queries<T> queries) {

        this.queries = queries;
        this.response = new AtomicReference<>(Results.empty());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onMessage(Message message) {

        LOG.info("|--> Received response: {}", message);
        response.set(new Results(Stream.of("foo", "bar", "baz")));
    }


    public Results<T> publish(RabbitTemplate rabbit, String queue, Runnable onDone) {

        try {
            var message = MessageBuilder.withBody("{}".getBytes()).setReplyTo(queue).build();
            rabbit.send("queries", queries.getQueryForTerm(), message);
            LOG.info("|<-- Published query: {}", queries.getQueryForTerm());
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
