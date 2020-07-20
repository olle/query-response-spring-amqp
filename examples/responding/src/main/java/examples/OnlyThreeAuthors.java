package examples;

import com.studiomediatech.queryresponse.ResponseBuilder;

import org.springframework.boot.context.event.ApplicationReadyEvent;

import org.springframework.context.event.EventListener;

import org.springframework.stereotype.Component;


//tag::class[]
@Component
public class OnlyThreeAuthors {

    private final ResponseBuilder responseBuilder;

    public OnlyThreeAuthors(ResponseBuilder responseBuilder) {

        this.responseBuilder = responseBuilder;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void respondWithAuthors() {

        responseBuilder.respondTo("authors", String.class)
            .withAll()
            .from("Tolkien", "Lewis", "Rowling");
    }
}
//end::class[]
