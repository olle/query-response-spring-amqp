package com.studiomediatech.queryresponse.ui.infra;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.studiomediatech.queryresponse.ui.app.telemetry.TelemetryService;

/**
 * Configuration for components of the application, with adapters for the platform.
 *
 */
@Configuration
@ComponentScan(basePackageClasses = InfraConfig.class)
public class InfraConfig {

    @Bean
    public TelemetryService queryResponseTelemetryService() {
        return new TelemetryService();
    }
}
