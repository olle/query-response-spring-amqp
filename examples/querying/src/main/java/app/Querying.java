package app;

import com.studiomediatech.Query;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.Collections;


@SpringBootApplication
class Querying implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {

        while (true) {
            System.out.println("Querying...");
            queryForSciFiBooks();
            Thread.sleep(10000);
        }
    }


    private void queryForSciFiBooks() {

        Query.queryFor("books/sci-fi")
            .waitingFor(2000)
            .orDefaults(Collections.emptyList());
    }


    public static void main(String[] args) {

        new SpringApplicationBuilder(Querying.class).web(WebApplicationType.NONE).run(args);
    }
}
