package app;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.studiomediatech.queryresponse.EnableQueryResponse;
import com.studiomediatech.queryresponse.QueryBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;


@SpringBootApplication
@EnableQueryResponse
class Querying implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(Querying.class);

    @Override
    public void run(String... args) throws Exception {

        LOG.info("About to begin querying...");
        Thread.sleep(3000);

        while (true) {
            queryForBooks();
            queryForAuthors();
            queryForNames();
        }
    }


    private void queryForBooks() throws InterruptedException {

        LOG.info("Publishing query..");

        var defaults = List.of("Neuromancer", "I, Robot");

        var results = QueryBuilder.queryFor("books/sci-fi", String.class)
                .waitingFor(2000)
                .orDefaults(defaults)
                .stream()
                .distinct()
                .collect(Collectors.toList());

        LOG.info("Results were: {} {}", results, results.equals(defaults) ? "(defaults)" : "");

        pause();
    }


    private void queryForAuthors() throws InterruptedException {

        LOG.info("Querying for authors...");

        var authors = QueryBuilder.queryFor("authors", Author.class)
                .takingAtLeast(3)
                .waitingFor(888)
                .orEmpty()
                .stream()
                .distinct()
                .collect(Collectors.toList());

        LOG.info("Results were: {} {}", authors, authors.isEmpty() ? "(not enough)" : "");

        pause();
    }


    private void queryForNames() throws InterruptedException {

        LOG.info("Querying for names...");

        var names = QueryBuilder.queryFor("names", String.class)
                .takingAtLeast(33)
                .takingAtMost(80)
                .waitingFor(456).orEmpty()
                .stream()
                .distinct()
                .collect(Collectors.toList());

        LOG.info("Results were: {} {} with size {}", names, names.isEmpty() ? "(not enough)" : "", names.size());

        pause();
    }


    private void pause() throws InterruptedException {

        int ms = ThreadLocalRandom.current().nextInt(1000, 20000);
        LOG.info("Sleeping for " + ms + "ms");
        Thread.sleep(ms);
    }


    public static void main(String[] args) {

        new SpringApplicationBuilder(Querying.class).web(WebApplicationType.NONE).run(args);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
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


        @Override
        public String toString() {

            return "Author [name=" + name + ", country=" + country + "]";
        }


        @Override
        public int hashCode() {

            return name.hashCode();
        }


        @Override
        public boolean equals(Object obj) {

            if (this == obj) {
                return true;
            }

            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }

            return ((Author) obj).name.equals(this.name);
        }
    }
}
