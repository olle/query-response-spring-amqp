package com.studiomediatech.queryresponse.ui.infra.adapter;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.studiomediatech.queryresponse.ui.app.telemetry.TelemetryService;
import com.studiomediatech.queryresponse.util.Loggable;

@Component
public class TelemetryServiceAdapter implements Loggable {

    private final TelemetryService service;

    public TelemetryServiceAdapter(TelemetryService service) {
        this.service = service;
    }

    @Scheduled(fixedDelayString = "PT3S")
    public void aFewSecondsHasPassed() {
        logger().info("TIC TOC!");
        service.publishNodes();
    }

}
