package app;

import com.studiomediatech.queryresponse.ResponseBuilder;

import org.springframework.boot.context.event.ApplicationReadyEvent;

import org.springframework.context.event.EventListener;

import org.springframework.core.annotation.Order;

import org.springframework.stereotype.Component;


// tag::response[]
@Component
public class Responses {

    @Order(1)
    @EventListener(ApplicationReadyEvent.class)
    public void response() {

        ResponseBuilder.respondTo("marco", String.class) // <1>
        .withAll() // <2>
        .from("polo", "yolo"); // <3>
    }
}
// end::response[]