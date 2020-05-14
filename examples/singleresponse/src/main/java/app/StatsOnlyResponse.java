package app;

import com.studiomediatech.queryresponse.EnableQueryResponse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@EnableQueryResponse
@RestController
public class StatsOnlyResponse {

    public static void main(String[] args) {

        SpringApplication.run(StatsOnlyResponse.class, args);
    }


    @GetMapping("/")
    public String helloWorld() {

        return "Hello World!";
    }
}
