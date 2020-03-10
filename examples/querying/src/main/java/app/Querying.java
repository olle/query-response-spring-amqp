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

import java.util.List;


@SpringBootApplication
@Import(QueryResponseConfiguration.class)
class Querying implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(Querying.class);

    @Override
    public void run(String... args) throws Exception {

        LOG.info("About to begin querying for books/sci-fi...");
        Thread.sleep(3000);

        var defaults = List.of("Neuromancer", "I, Robot");

        while (true) {
            LOG.info("Publishing query..");

            var results = Queries.queryFor("books/sci-fi", String.class)
                    .waitingFor(4000)
                    .orDefaults(defaults);

            LOG.info("Results were: {} {}", results, results.equals(defaults) ? "(defaults)" : "");

            LOG.info("Sleeping for 10s...");
            Thread.sleep(10000);
        }
    }


    public static void main(String[] args) {

        new SpringApplicationBuilder(Querying.class).web(WebApplicationType.NONE).run(args);
    }
}
