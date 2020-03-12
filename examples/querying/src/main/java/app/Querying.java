package app;

import com.studiomediatech.queryresponse.Queries;
import com.studiomediatech.queryresponse.QueryResponseConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import org.springframework.context.annotation.Import;

import java.time.temporal.ChronoUnit;

import java.util.List;


@SpringBootApplication
@Import(QueryResponseConfiguration.class)
class Querying implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(Querying.class);

    @Override
    public void run(String... args) throws Exception {

        LOG.info("About to begin querying...");
        Thread.sleep(3000);

        while (true) {
            queryForBooks(List.of("Neuromancer", "I, Robot"));
            queryForAuthors();
        }
    }


    private void queryForBooks(List<String> defaults) throws InterruptedException {

        LOG.info("Publishing query..");

        var results = Queries.queryFor("books/sci-fi", String.class)
                .waitingFor(4000)
                .orDefaults(defaults);

        LOG.info("Results were: {} {}", results, results.equals(defaults) ? "(defaults)" : "");

        LOG.info("Sleeping for 10s...");
        Thread.sleep(10000);
    }


    private void queryForAuthors() throws InterruptedException {

        LOG.info("Querying for authors...");

        try {
            var authors = Queries.queryFor("authors", Author.class)
                    .takingAtLeast(3)
                    .waitingFor(2L, ChronoUnit.SECONDS)
                    .orThrow(() -> new IllegalStateException("Not enough authors!"));

            LOG.info("Results were: {}", authors);
        } catch (RuntimeException e) {
            LOG.error("Query for authors failed", e);
        }

        LOG.info("Sleeping for 10s...");
        Thread.sleep(10000);
    }


    public static void main(String[] args) {

        new SpringApplicationBuilder(Querying.class).web(WebApplicationType.NONE).run(args);
    }

    static class Author {

        public String name;
        public String country;

        public String getName() {

            return name;
        }


        public void setName(String name) {

            this.name = name;
        }


        public String getCountry() {

            return country;
        }


        public void setCountry(String country) {

            this.country = country;
        }
    }
}
