package app;

import com.studiomediatech.queryresponse.QueryBuilder;

import org.springframework.boot.context.event.ApplicationReadyEvent;

import org.springframework.context.event.EventListener;

import org.springframework.core.annotation.Order;

import org.springframework.stereotype.Component;


// tag::query[]
@Component
public class Queries {

    @Order(2)
    @EventListener(ApplicationReadyEvent.class)
    public void query() {

        QueryBuilder.queryFor("marco", String.class)
            .waitingFor(1000L)
            .orEmpty()
            .stream()
            .forEach(System.out::println);
    }
}
// end::query[]
