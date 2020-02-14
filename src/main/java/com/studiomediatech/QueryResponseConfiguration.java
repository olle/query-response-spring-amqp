package com.studiomediatech;

import com.studiomediatech.responses.RespondingRegistry;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class QueryResponseConfiguration {

    @Bean
    RespondingRegistry respondingRegistry() {

        return new RespondingRegistry();
    }
}
