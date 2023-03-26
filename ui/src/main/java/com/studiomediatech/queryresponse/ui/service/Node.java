package com.studiomediatech.queryresponse.ui.service;

import java.util.UUID;

public final class Node {

    private final UUID uuid;

    private Node(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "Node [uuid=" + uuid + "]";
    }

    public static Node from(UUID uuid) {
        return new Node(uuid);
    }
}
