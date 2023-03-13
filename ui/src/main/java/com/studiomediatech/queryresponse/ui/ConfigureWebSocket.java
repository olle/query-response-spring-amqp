package com.studiomediatech.queryresponse.ui;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.studiomediatech.queryresponse.ui.api.WebSocketApiHandler;

@Order(100)
@Configuration
class ConfigureWebSocket implements WebSocketConfigurer {

    private final WebSocketApiHandler webSocketHandler;

    public ConfigureWebSocket(WebSocketApiHandler webSocketHandler) {

        this.webSocketHandler = webSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        // TODO: DO NOT ALLOW ORIGINS * !!!
        registry.addHandler(webSocketHandler, "/ws").setAllowedOrigins("*");
    }
}