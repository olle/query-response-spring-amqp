package app;

import com.studiomediatech.queryresponse.EnableQueryResponse;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;


@SpringBootApplication
@EnableQueryResponse
class StatsOnlyResponse implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {

        System.out.println("> Started, will register for stats queries. Press CTRL-C to exit.");
        Thread.currentThread().join();
    }


    public static void main(String[] args) {

        new SpringApplicationBuilder(StatsOnlyResponse.class).web(WebApplicationType.NONE).run(args);
    }
}
