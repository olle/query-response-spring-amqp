package app;

import com.studiomediatech.queryresponse.Queries;
import com.studiomediatech.queryresponse.QueryResponseConfiguration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import org.springframework.context.annotation.Import;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@SpringBootApplication
@Import(QueryResponseConfiguration.class)
class Querying implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {

        println("About to begin querying for books/sci-fi...");
        Thread.sleep(3000);

        while (true) {
            println("Publishing query..");

            var defaults = List.of("Neuromancer", "I, Robot");

            var results = Queries.<String>queryFor("books/sci-fi")
                    .waitingFor(4000)
                    .orDefaults(defaults)
                    .collect(Collectors.toList());

            println("Results were: %s %s", results, results.equals(defaults) ? "(defaults)" : "");

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
