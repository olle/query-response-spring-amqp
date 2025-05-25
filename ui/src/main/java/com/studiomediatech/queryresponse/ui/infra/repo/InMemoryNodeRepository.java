package com.studiomediatech.queryresponse.ui.infra.repo;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.studiomediatech.queryresponse.ui.app.telemetry.Node;
import com.studiomediatech.queryresponse.ui.app.telemetry.NodeRepository;

public class InMemoryNodeRepository implements NodeRepository {

    private final Map<UUID, Node> nodes = new ConcurrentHashMap<>();

    @Override
    public Optional<Node> findOneByUUID(UUID uuid) {
        return Optional.ofNullable(nodes.get(uuid));
    }

    @Override
    public void save(Node node) {
        Node copy = new Node(node);
        nodes.put(copy.getUUID(), copy);
    }

    @Override
    public Collection<Node> findAll() {
        return nodes.values().stream().sorted(Node.SORT).toList();
    }
}
