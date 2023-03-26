package com.studiomediatech.queryresponse.ui.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import com.studiomediatech.queryresponse.EnableQueryResponse;
import com.studiomediatech.queryresponse.ui.messaging.Messaging;

@EnableWebSocket
@EnableScheduling
@EnableQueryResponse
@SpringBootApplication
public class QueryResponseUIApp {

    public static void main(String[] args) {
        SpringApplication.run(QueryResponseUIApp.class);
    }

    @Configuration
    @Import({ Messaging.class })
    static class Setup {
    }
}
