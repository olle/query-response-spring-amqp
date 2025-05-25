package com.studiomediatech.queryresponse.ui.app.telemetry;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface NodeRepository {

    Optional<Node> findOneByUUID(UUID uuid);

    void save(Node node);

    Collection<Node> findAll();
}
