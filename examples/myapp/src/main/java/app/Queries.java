package app;

import com.studiomediatech.queryresponse.QueryBuilder;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.context.event.ApplicationReadyEvent;

import org.springframework.context.event.EventListener;

import org.springframework.core.annotation.Order;

import org.springframework.stereotype.Component;

import java.util.Collection;


// tag::query[]
@Component
public class Queries {

    @Autowired
    QueryBuilder queryBuilder;

    @Order(2)
    @EventListener(ApplicationReadyEvent.class)
    public void query() {

        Collection<String> polos =
            queryBuilder.queryFor("marco", String.class) // <1>
            .waitingFor(1000L) // <2>
            .orEmpty(); // <3>

        polos.stream().map("marco? "::concat).forEach(System.out::println);
    }
}
// end::query[]
