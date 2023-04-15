package com.studiomediatech.queryresponse.ui.api;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Order(100)
@Configuration
class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketApiHandlerPort webSocketHandler;

    public WebSocketConfig(WebSocketApiHandlerPort webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // TODO: DO NOT ALLOW ORIGINS * !!!
        registry.addHandler(webSocketHandler, "/ws").setAllowedOrigins("*");
    }
}