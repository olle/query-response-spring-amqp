package app;

import com.studiomediatech.QueryResponseConfiguration;
import com.studiomediatech.Responses;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import org.springframework.context.annotation.Import;

import java.util.Date;


@SpringBootApplication
@Import(QueryResponseConfiguration.class)
class Responding implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {

        println("Registering responses...");

        Responses.respondTo("books/sci-fi")
            .withAll()
            .from("William Gibson", "Isaac Asimov", "J.R.R. Tolkien");

        println("Waiting for queries! Press CTRL-C to exit.");
        Thread.currentThread().join();
    }


    private static void println(String message, Object... args) {

        System.out.println("> " + new Date() + "\t: " + String.format(message, args));
    }


    public static void main(String[] args) {

        new SpringApplicationBuilder(Responding.class).web(WebApplicationType.NONE).run(args);
    }
}
