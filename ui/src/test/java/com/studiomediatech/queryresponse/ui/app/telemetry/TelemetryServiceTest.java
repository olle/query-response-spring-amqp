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
                .containsEntry("count_fallbacks", 21L).containsEntry("quality", 0.0).containsEntry("coverage", 0.0)
                .containsKey("telemetry_at");
        assertThat((List<?>) metrics.get("quality_history")).hasSize(3);
        assertThat((List<?>) metrics.get("avg_latencies")).hasSize(3);

        assertThat(webSocket.nodes).hasSize(1);

        Map<String, List<Stat>> nodes = webSocket.nodes.getLast();
        assertThat(nodes).containsKey(NODE_UUID);
        assertThat(nodes.get(NODE_UUID)).anyMatch(stat -> Stat.AVG_THROUGHPUT.equals(stat.key()));

        assertThat(repository.findAll()).hasSize(1);
        assertThat(repository.findAll().iterator().next().getName()).isEqualTo("querying-example");
    }

    @Test
    void shouldCalculateQualityFromResponsesFallbacksAndQueries() {

        var webSocket = new CapturingWebSocketApiAdapter();
        var repository = new InMemoryNodeRepository();
        var service = new TelemetryService(webSocket, repository, QueryPublisherAdapter.empty());

        service.handleConsumed(new Stats(List.of(new Stat(Stat.COUNT_QUERIES, 10L, null, NODE_UUID),
                new Stat(Stat.COUNT_CONSUMED_RESPONSES, 8L, null, NODE_UUID),
                new Stat(Stat.COUNT_FALLBACKS, 3L, null, NODE_UUID))));

        service.publish();

        Map<String, Object> metrics = webSocket.metrics.getLast();
        assertThat(metrics).containsEntry("count_queries", 10L).containsEntry("count_responses", 8L)
                .containsEntry("count_fallbacks", 3L).containsEntry("quality", 50.0).containsEntry("coverage", 80.0);
        assertThat(metrics.get("quality_history")).isEqualTo(List.of(50.0));
    }

    @Test
    void shouldIgnoreUnknownTelemetryProperties() throws Exception {

        Stats stats = mapper.readValue("{\"elements\":[],\"future\":true}", Stats.class);

        assertThat(stats.elements()).isEmpty();
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
