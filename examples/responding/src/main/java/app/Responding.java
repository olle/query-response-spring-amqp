package app;

import com.studiomediatech.queryresponse.EnableQueryResponse;
import com.studiomediatech.queryresponse.ResponseBuilder;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import org.springframework.context.event.EventListener;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


@SpringBootApplication
@EnableQueryResponse
public class Responding {

    @Autowired
    ResponseBuilder responseBuilder;

    public static void main(String[] args) {

        new SpringApplicationBuilder(Responding.class)
            .web(WebApplicationType.NONE)
            .run(args);
    }


    @EventListener(ApplicationReadyEvent.class)
    void respondToBooks() {

        responseBuilder.respondTo("books/sci-fi", String.class)
            .withAll()
            .from("Neuromancer", "Snow Crash", "I, Robot", "The Gods Themselves", "Pebble in the Sky");
    }


    @EventListener(ApplicationReadyEvent.class)
    void respondToAuthors() {

        Author tolkien = new Author("J. R. R. Tolkien", 1892, "South Africa");
        Author lewis = new Author("C. S. Lewis", 1898, "United Kingdom");
        Author asimov = new Author("Isaac Asimov", 1920, "Russia");
        Author gibson = new Author("William Gibson", 1948, "United States");

        List<Author> authors = Arrays.asList(tolkien, lewis, asimov, gibson);

        responseBuilder.respondTo("authors", Author.class)
            .withAll()
            .suppliedBy(() -> authors.subList(0, ThreadLocalRandom.current().nextInt(1, authors.size() + 1)));
    }


    @EventListener(ApplicationReadyEvent.class)
    void respondToNames() {

        List<String> names = Arrays.asList("Yasir", "Araceli", "Emídio", "Rebekka", "Jack", "Hertha", "Oscar",
                "Astrid", "Sedef", "Naomi", "Ioque", "Davut", "Edith", "Ortrun", "Eddie", "Noah", "Anthony", "Connor",
                "Mestan", "Erich", "Marius", "Adrian", "Jenny", "Enio", "Grazia", "Batur", "Fabien", "Oscar", "Rafael",
                "Efe", "Arthur", "Eva", "Thea", "Finn", "Esat", "Ramon", "Amanda", "Anouchka", "Zachary", "Ece",
                "Hans-Günther", "Ebenezer", "Loni", "Ava", "Rose", "Lucy", "Katarina", "Ana", "Jonathan", "Bertram",
                "Balthasar", "Fletcher", "Annelene", "Alberto", "Matilda", "Juanita", "Levin", "Latife", "Alexandre",
                "Dylan", "آریا", "Carola", "Oswald", "Noury", "Logan", "Oliver", "Patricia", "Reitze", "Hayley",
                "Saya", "Alexa", "Clarindo", "Mestan", "Nadine", "Salome", "Erin", "Elina", "Charlie", "Juliette",
                "Cindy", "Hannah", "Victoria", "Eleanor", "Alexander", "Lázaro", "Daniel", "Sally", "Ashton");

        responseBuilder.respondTo("names", String.class)
            .withBatchesOf(12)
            .from(names);
    }

    static class Author {

        public String name;
        public int year;
        public String country;

        public Author(String name, int year, String country) {

            this.name = name;
            this.year = year;
            this.country = country;
        }
    }
}
