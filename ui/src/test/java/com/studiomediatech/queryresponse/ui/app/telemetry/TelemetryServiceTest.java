package com.studiomediatech.queryresponse.ui.app.telemetry;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studiomediatech.queryresponse.ui.app.adapter.QueryPublisherAdapter;
import com.studiomediatech.queryresponse.ui.app.adapter.WebSocketApiAdapter;
import com.studiomediatech.queryresponse.ui.infra.repo.InMemoryNodeRepository;
import com.studiomediatech.queryresponse.ui.messaging.Stat;
import com.studiomediatech.queryresponse.ui.messaging.Stats;

class TelemetryServiceTest {

    private static final String NODE_UUID = "ce45dce0-3ff2-4cb7-b2bd-caa72a675542";

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void shouldPublishMetricsAndNodesFromConsumedStats() throws Exception {

        var webSocket = new CapturingWebSocketApiAdapter();
        var repository = new InMemoryNodeRepository();
        var service = new TelemetryService(webSocket, repository, QueryPublisherAdapter.empty());

        service.handleConsumed(read("example-internal-stats-1.json"));
        service.handleConsumed(read("example-internal-stats-2.json"));
        service.handleConsumed(read("example-internal-stats-3.json"));

        service.publish();

        assertThat(webSocket.metrics).hasSize(1);

        Map<String, Object> metrics = webSocket.metrics.getLast();
        assertThat(metrics).containsEntry("count_queries", 21L).containsEntry("count_responses", 0L)
                .containsEntry("count_fallbacks", 21L).containsEntry("success_rate", 0.0);
        assertThat((List<?>) metrics.get("success_rates")).hasSize(3);
        assertThat((List<?>) metrics.get("avg_latencies")).hasSize(3);

        assertThat(webSocket.nodes).hasSize(1);

        Map<String, List<Stat>> nodes = webSocket.nodes.getLast();
        assertThat(nodes).containsKey(NODE_UUID);
        assertThat(nodes.get(NODE_UUID)).anyMatch(stat -> Stat.AVG_THROUGHPUT.equals(stat.key()));

        assertThat(repository.findAll()).hasSize(1);
        assertThat(repository.findAll().iterator().next().getName()).isEqualTo("querying-example");
    }

    private Stats read(String name) throws Exception {

        try (var in = getClass().getResourceAsStream("/" + name)) {
            return mapper.readValue(in, Stats.class);
        }
    }

    private static final class CapturingWebSocketApiAdapter implements WebSocketApiAdapter {

        private final List<Map<String, Object>> metrics = new ArrayList<>();
        private final List<Map<String, List<Stat>>> nodes = new ArrayList<>();

        @Override
        public void publishMetrics(Map<String, Object> metrics) {
            this.metrics.add(metrics);
        }

        @Override
        public void publishNodes(Map<String, List<Stat>> nodes) {
            this.nodes.add(nodes);
        }
    }
}
