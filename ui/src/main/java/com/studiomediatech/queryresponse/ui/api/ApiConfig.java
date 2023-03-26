package com.studiomediatech.queryresponse.ui.api;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

/**
 * Configuration for port components, providing access or information over published APIs, specifically over HTTP and
 * WebSockets.
 */
@Configuration
@EnableWebSocket
@ComponentScan(basePackageClasses = ApiConfig.class)
public class ApiConfig {
    // OK
}
