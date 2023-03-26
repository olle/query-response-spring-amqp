package com.studiomediatech.queryresponse.ui.service;

import org.springframework.stereotype.Service;

import com.studiomediatech.queryresponse.stats.Stat;
import com.studiomediatech.queryresponse.stats.Stats;
import com.studiomediatech.queryresponse.util.Logging;

@Service
public class QueryResponseStatsService implements Logging, StatsHandlerAdapter {

    // private final NodesRepository nodesRepository;
    //
    // public QueryResponseStatsService(NodesRepository nodesRepository) {
    // this.nodesRepository = nodesRepository;
    // }

    @Override
    public void handleConsumed(Stats stats) {

        log().info("Consumed stats with {} elements", stats.elements().size());

        // stats.elements().stream().map(Stat::uuid).map(UUID::fromString).map(Node::from).forEach(nodesRepository::save);

        stats.elements().stream().filter(s -> s.timestamp() != null).map(Stat::toString)
                .forEach(str -> log().debug("STAT VALUE: {}", str));

        stats.elements().stream().filter(s -> s.timestamp() == null).map(Stat::toString)
                .forEach(str -> log().debug("STAT INFO: {}", str));

    }
}
