package com.studiomediatech;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.studiomediatech.QueryResponseUI.Querier;

import com.studiomediatech.queryresponse.util.Logging;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

import java.nio.charset.StandardCharsets;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


public class WebSocket extends TextWebSocketHandler implements Logging {

    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    private final EventEmitter emitter;
    private final ObjectMapper objectMapper;

    public WebSocket(EventEmitter emitter) {

        this.emitter = emitter;

        objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(Include.NON_NULL);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        sessions.add(session);
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

        sessions.remove(session);
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        System.err.println("GOT MESSAGE " + message + " FROM SESSION " + session);
    }


    public void handleCountQueriesAndResponses(long countQueriesSum, long countResponsesSum, long countFallbacksSum,
        double successRate, List<Double> successRates) {

        var json = String.format(Locale.US,
                "{\"metrics\": {"
                + "\"count_queries\": %d,"
                + "\"count_responses\": %d,"
                + "\"count_fallbacks\": %d,"
                + "\"success_rate\": %f,"
                + "\"success_rates\": %s"
                + "}}", countQueriesSum, countResponsesSum, countFallbacksSum, successRate, successRates);

        publishTextMessageWithPayload(json);
    }


    public void handleLatency(long minLatency, long maxLatency, double avgLatency, List<Double> latencies) {

        var json = String.format(Locale.US,
                "{\"metrics\": {"
                + (minLatency != -1 ? "\"min_latency\": %d," : "") + (maxLatency != -1 ? "\"max_latency\": %d," : "")
                + "\"avg_latency\": %f,"
                + "\"avg_latencies\": %s"
                + "}}", minLatency, maxLatency, avgLatency, latencies);

        publishTextMessageWithPayload(json);
    }


    public void handleThroughput(double queries, double responses, double avg, List<Double> throughputs) {

        var json = String.format(Locale.US,
                "{\"metrics\": {"
                + "\"throughput_queries\": %f,"
                + "\"throughput_responses\": %f,"
                + "\"avg_throughput\": %f,"
                + "\"avg_throughputs\": %s"
                + "}}", queries, responses, avg, throughputs);

        publishTextMessageWithPayload(json);
    }


    public void handleNodes(Map<String, List<Querier.Stat>> nodes) {

        try {
            StringBuilder sb = new StringBuilder();

            sb.append("{\"nodes\": ");
            sb.append(objectMapper.writeValueAsString(nodes));
            sb.append("}");

            publishTextMessageWithPayload(sb.toString());
        } catch (JsonProcessingException e) {
            log().error("Failed to create nodes payload", e);
        }
    }


    private void publishTextMessageWithPayload(String json) {

        var message = new TextMessage(json.getBytes(StandardCharsets.UTF_8));

        for (WebSocketSession s : sessions) {
            try {
                s.sendMessage(message);
            } catch (IOException e) {
                log().error("Could not publish text message to websocket", e);
            }
        }
    }
}
