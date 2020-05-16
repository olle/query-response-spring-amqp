package app;

import com.studiomediatech.queryresponse.EnableQueryResponse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@EnableQueryResponse
public class MyApp {

    public static void main(String[] args) {

        SpringApplication.run(MyApp.class, args);
    }
}
