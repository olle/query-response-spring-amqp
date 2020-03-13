package com.studiomediatech;

import com.studiomediatech.queryresponse.QueryResponseConfiguration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import org.springframework.context.annotation.Import;


@SpringBootApplication
@Import(QueryResponseConfiguration.class)
class QueryResponseMonitoringUI implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {

        System.out.println("BYE!");
    }


    public static void main(String[] args) {

        new SpringApplicationBuilder(QueryResponseMonitoringUI.class).web(WebApplicationType.NONE).run(args);
    }
}
