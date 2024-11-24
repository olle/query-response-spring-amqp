package com.studiomediatech.queryresponse.ui.infra.adapter;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.studiomediatech.queryresponse.ui.app.telemetry.TelemetryService;
import com.studiomediatech.queryresponse.util.Logging;

@Component
public class TelemetryServiceAdapter implements Logging {

    private final TelemetryService service;

    public TelemetryServiceAdapter(TelemetryService service) {
        this.service = service;
    }

    @Scheduled(fixedDelayString = "PT3S")
    public void aFewSecondsHasPassed() {
        log().info("TIC TOC!");
        service.publishNodes();
    }

}
