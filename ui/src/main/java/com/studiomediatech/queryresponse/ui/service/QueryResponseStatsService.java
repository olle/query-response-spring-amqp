package com.studiomediatech.queryresponse.ui.service;

import org.springframework.stereotype.Service;

import com.studiomediatech.queryresponse.stats.Stat;
import com.studiomediatech.queryresponse.stats.Stats;
import com.studiomediatech.queryresponse.util.Logging;

@Service
public class QueryResponseStatsService implements Logging, StatsHandlerAdapter {

    @Override
    public void handleConsumed(Stats stats) {
        stats.elements().stream().filter(s -> s.timestamp() != null).map(Stat::toString)
                .forEach(str -> log().debug("STAT VALUE: {}", str));

        stats.elements().stream().filter(s -> s.timestamp() == null).map(Stat::toString)
                .forEach(str -> log().debug("STAT INFO: {}", str));
    }
}
