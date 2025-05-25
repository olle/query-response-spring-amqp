package com.studiomediatech.queryresponse.ui.app.telemetry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.studiomediatech.queryresponse.ui.app.adapter.QueryPublisherAdapter;
import com.studiomediatech.queryresponse.ui.app.adapter.RestApiAdapter;
import com.studiomediatech.queryresponse.ui.app.adapter.TelemetryHandlerAdapter;
import com.studiomediatech.queryresponse.ui.app.adapter.WebSocketApiAdapter;
import com.studiomediatech.queryresponse.ui.messaging.Stat;
import com.studiomediatech.queryresponse.ui.messaging.Stats;
import com.studiomediatech.queryresponse.util.Loggable;

public class TelemetryService implements Loggable, TelemetryHandlerAdapter, RestApiAdapter {

    private final WebSocketApiAdapter webSocketApiAdapter;
    private final NodeRepository nodeRepository;
    private final QueryPublisherAdapter queryPublisherAdapter;

    public TelemetryService(WebSocketApiAdapter webSocketApiAdapter, NodeRepository nodeRepository,
            QueryPublisherAdapter queryPublisherAdapter) {
        this.webSocketApiAdapter = webSocketApiAdapter;
        this.nodeRepository = nodeRepository;
        this.queryPublisherAdapter = queryPublisherAdapter;
    }

    @Override
    public Map<String, Object> query(String q, int timeout, int limit) {
        return queryPublisherAdapter.query(q, timeout, limit);
    }

    @Override
    public Map<String, Object> nodes() {
        return Map.of("elements", nodeRepository.findAll());
    }

    @Override
    public void handleConsumed(Stats stats) {

        logger().info("Consumed stats with {} elements", stats.elements().size());

        Collection<Node> nodes = parseToNodesCollection(stats);
        updateNodes(nodes);

        // stats.elements().stream().filter(s -> s.timestamp() !=
        // null).map(Stat::toString)
        // .forEach(str -> log().debug("STAT VALUE: {}", str));
        //
        // stats.elements().stream().filter(s -> s.timestamp() ==
        // null).map(Stat::toString)
        // .forEach(str -> log().debug("STAT INFO: {}", str));
    }

    private void updateNodes(Collection<Node> nodes) {
        for (Node node : nodes) {
            Optional<Node> maybe = nodeRepository.findOneByUUID(node.getUUID());
            if (maybe.isPresent()) {
                Node updated = maybe.get().update(node);
                nodeRepository.save(updated);
            } else {
                nodeRepository.save(node);
            }
        }
    }

    protected Collection<Node> parseToNodesCollection(Stats stats) {

        Map<UUID, Node> nodes = new HashMap<>();

        for (Stat stat : stats.elements()) {

            UUID uuid = UUID.fromString(stat.uuid());
            Node node = nodes.computeIfAbsent(uuid, key -> Node.from(key));

            stat.whenKey("name", node::setName);
            stat.whenKey("pid", node::setPid);
            stat.whenKey("host", node::setHost);
            stat.whenKey("uptime", node::setUptime);
        }

        return nodes.values();
    }

    public void publishNodes() {
        webSocketApiAdapter.publishNodes(nodeRepository.findAll());
    }

}
