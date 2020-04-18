package com.studiomediatech.queryresponse;

import com.studiomediatech.queryresponse.util.Logging;

import org.springframework.context.ApplicationContext;

import org.springframework.core.env.Environment;

import org.springframework.util.StringUtils;

import java.lang.management.ManagementFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.time.Duration;

import java.util.List;
import java.util.stream.Stream;


class Statistics implements Logging {

    private final Environment env;
    private final ApplicationContext ctx;

    public Statistics(Environment env, ApplicationContext ctx) {

        this.env = env;
        this.ctx = ctx;

        ResponseBuilder.respondTo("query-response/stats", String.class)
            .withAll()
            .suppliedBy(() ->
                    List.of( // NOSNAR
                        getApplicationNameOrDefault("app"), // NOSONAR
                        getHostnameOrDefault("unknown"), // NOSONAR
                        getPidOrDefault("-"), // NOSONAR
                        getUptimeOrDefault("-")));
    }

    private String getUptimeOrDefault(String defaults) {

        return Stream.of(Duration.ofMillis(ManagementFactory.getRuntimeMXBean().getUptime()).toString())
            .filter(StringUtils::hasText)
            .findFirst()
            .orElse(defaults);
    }


    private String getPidOrDefault(String defaults) {

        try {
            return "" + ProcessHandle.current().pid();
        } catch (UnsupportedOperationException e) {
            // Dang!
        }

        return defaults;
    }


    private String getHostnameOrDefault(String defaults) {

        String hostName = null;

        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            // Meh!
        }

        return Stream.of(System.getProperty("HOSTNAME"), hostName, System.getProperty("COMPUTERNAME"))
            .filter(StringUtils::hasText)
            .findFirst()
            .orElse(defaults);
    }


    private String getApplicationNameOrDefault(String defaults) {

        return Stream.of(env.getProperty("cola.id"), env.getProperty("spring.application.name"),
                    ctx.getApplicationName(), ctx.getId(), ctx.getDisplayName())
            .filter(StringUtils::hasText)
            .findFirst()
            .orElse(defaults);
    }
}
