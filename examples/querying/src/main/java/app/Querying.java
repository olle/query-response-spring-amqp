package app;

import com.studiomediatech.Query;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.Collections;
import java.util.Date;


@SpringBootApplication
class Querying implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {

        while (true) {
            println("Querying..");

            var results = Query.queryFor("books/sci-fi")
                    .waitingFor(2000)
                    .orDefaults(Collections.emptyList());

            println("Results were: %s", results);

            println("Sleeping for 10s...");
            Thread.sleep(10000);
        }
    }


    private static void println(String message, Object... args) {

        System.out.println(new Date() + " - " + String.format(message, args));
    }


    public static void main(String[] args) {

        new SpringApplicationBuilder(Querying.class).web(WebApplicationType.NONE).run(args);
    }
}
