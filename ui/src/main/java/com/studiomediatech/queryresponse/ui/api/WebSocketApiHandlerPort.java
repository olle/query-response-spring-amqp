package com.studiomediatech.queryresponse.ui.api;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.studiomediatech.events.QueryRecordedEvent;
import com.studiomediatech.queryresponse.ui.app.adapter.EventEmitterAdapter;
import com.studiomediatech.queryresponse.ui.app.adapter.WebSocketApiAdapter;
import com.studiomediatech.queryresponse.ui.messaging.Stat;
import com.studiomediatech.queryresponse.util.Loggable;

@Component
public class WebSocketApiHandlerPort extends TextWebSocketHandler implements WebSocketApiAdapter, Loggable {

    private static final int SEND_TIME_LIMIT = 6 * 1000;
    private static final int SEND_BUFFER_SIZE_LIMIT = 512 * 1024;

    private final Map<String, WebSocketSession> sessionsById = new ConcurrentHashMap<>();

    private final EventEmitterAdapter emitter;
    private final ObjectMapper objectMapper;

    public WebSocketApiHandlerPort(EventEmitterAdapter emitter) {

        this.emitter = emitter;

        objectMapper = JsonMapper.builder().serializationInclusion(Include.NON_NULL).build();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        logger().info("CONNECTED {}", session);

        sessionsById.put(session.getId(),
                new ConcurrentWebSocketSessionDecorator(session, SEND_TIME_LIMIT, SEND_BUFFER_SIZE_LIMIT));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

        sessionsById.remove(session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        if ("ping".equals(message.getPayload())) {
            session.sendMessage(new TextMessage("pong"));
            return;
        }

        emitter.emitEvent(QueryRecordedEvent.valueOf(message.getPayload(), session.getId()));
    }

    @Override
    public void publishMetrics(Map<String, Object> metrics) {
        publish("metrics", metrics);
    }

    @Override
    public void publishNodes(Map<String, List<Stat>> nodes) {
        publish("nodes", nodes);
    }

    private void publish(String key, Object payload) {

        try {
            publishTextMessageWithPayload(objectMapper.writeValueAsString(Map.of(key, payload)));
        } catch (JsonProcessingException e) {
            logger().error("Failed to create " + key + " payload", e);
        }
    }

    private void publishTextMessageWithPayload(String json) {

        TextMessage message = new TextMessage(json.getBytes(StandardCharsets.UTF_8));

        for (WebSocketSession s : sessionsById.values()) {
            try {
                s.sendMessage(message);
            } catch (IOException e) {
                logger().error("Could not publish text message to websocket", e);
            }
        }
    }

    @Override
    public void publishResponse(Collection<Object> response, String id) {

        try {
            publishTextMessageWithPayloadToSession(id, objectMapper.writeValueAsString(Map.of("response", response)));
        } catch (JsonProcessingException e) {
            logger().error("Failed to create response payload", e);
        }
    }

    private void publishTextMessageWithPayloadToSession(String id, String json) {

        TextMessage message = new TextMessage(json.getBytes(StandardCharsets.UTF_8));

        WebSocketSession s = sessionsById.get(id);

        if (s == null) {
            logger().warn("Could not find websocket session for {} in {}", id, sessionsById);

            return;
        }

        try {
            s.sendMessage(message);
        } catch (IOException e) {
            logger().error("Could not publish text message to websocket", e);
        }
    }
}
