package com.studiomediatech.queryresponse.ui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import com.studiomediatech.queryresponse.EnableQueryResponse;

@SpringBootApplication
@EnableQueryResponse
@EnableScheduling
@EnableWebSocket
public class QueryResponseUIApp {
    public static void main(String[] args) {
        SpringApplication.run(QueryResponseUIApp.class);
    }
}
