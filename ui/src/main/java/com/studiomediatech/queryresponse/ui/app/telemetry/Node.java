package com.studiomediatech.queryresponse.ui.app.telemetry;

import java.util.Comparator;
import java.util.UUID;

import org.springframework.util.Assert;

import com.studiomediatech.queryresponse.util.Loggable;

public class Node implements Loggable {

    public static final Comparator<Node> SORT = Comparator.comparing(Node::getName).thenComparing(Node::getUUID);

    protected final UUID uuid;
    protected String name;
    protected String pid;
    protected String host;
    protected String uptime;

    public Node(UUID uuid) {
        this.uuid = uuid;
    }

    public Node(Node other) {
        this(other.uuid);
        this.name = other.name;
        this.pid = other.pid;
        this.host = other.host;
        this.uptime = other.uptime;
    }

    public static Node from(UUID uuid) {
        return new Node(uuid);
    }

    public Node update(Node other) {

        Assert.isTrue(other.uuid.equals(this.uuid), "Must be same node.");

        logger().info("Updating {} with {}", this, other);

        this.host = other.host;
        this.name = other.name;
        this.uptime = other.uptime;
        this.pid = other.pid;

        return this;
    }

    @Override
    public String toString() {
        return "Node [uuid=%s]".formatted(uuid);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }

    public UUID getUUID() {
        return uuid;
    }

}
