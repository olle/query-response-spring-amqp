package com.studiomediatech.queryresponse.ui.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.studiomediatech.queryresponse.ui.api.ApiConfig;
import com.studiomediatech.queryresponse.ui.infra.InfraConfig;
import com.studiomediatech.queryresponse.ui.messaging.MessagingConfig;

@EnableScheduling
@SpringBootApplication
public class QueryResponseUIApp {

    public static final String QUERY_RESPONSE_STATS_QUEUE_BEAN = "queryResponseStatsBean";

    public static void main(String[] args) {
        SpringApplication.run(QueryResponseUIApp.class);
    }

    @Configuration
    @Import({ MessagingConfig.class, ApiConfig.class, InfraConfig.class })
    static class Setup {
        // OK
    }
}
