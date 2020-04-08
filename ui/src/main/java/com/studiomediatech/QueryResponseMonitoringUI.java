package com.studiomediatech;

import com.studiomediatech.queryresponse.EnableQueryResponse;
import com.studiomediatech.queryresponse.QueryBuilder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;


@SpringBootApplication
@EnableQueryResponse
@EnableScheduling
class QueryResponseMonitoringUI {

    public static void main(String[] args) {

        SpringApplication.run(QueryResponseMonitoringUI.class);
    }


    @Scheduled(fixedDelay = 1000 * 20)
    void query() {

        QueryBuilder.queryFor("query-response/stats", Object.class)
            .waitingFor(1000)
            .orEmpty()
            .forEach(obj -> System.out.println("Got this: " + obj));
    }
}
