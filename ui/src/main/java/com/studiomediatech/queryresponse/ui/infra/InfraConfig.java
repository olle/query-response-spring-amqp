package com.studiomediatech.queryresponse.ui.infra;

import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.studiomediatech.queryresponse.ui.app.adapter.EventEmitterAdapter;
import com.studiomediatech.queryresponse.ui.app.adapter.WebSocketApiAdapter;
import com.studiomediatech.queryresponse.ui.app.telemetry.TelemetryService;

/**
 * Configuration for components of the application, with adapters for the platform.
 */
@Configuration
@ComponentScan(basePackageClasses = InfraConfig.class)
public class InfraConfig {

    @Bean
    @Primary
    TaskScheduler taskScheduler() {
        return new ThreadPoolTaskScheduler();
    }

    @Bean
    EventEmitterAdapter eventEmitter(TaskScheduler scheduler, ApplicationEventPublisher publisher) {
        return new AsyncEventEmitter(scheduler, publisher);
    }

    @Bean
    public TelemetryService telemetryService(Optional<WebSocketApiAdapter> optionalWebSocketApiAdapter) {
        return new TelemetryService(optionalWebSocketApiAdapter.orElse(WebSocketApiAdapter.empty()));
    }
}
