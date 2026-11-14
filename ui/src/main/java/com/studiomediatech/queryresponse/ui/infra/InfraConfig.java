package com.studiomediatech.queryresponse.ui.infra;

import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.studiomediatech.queryresponse.ui.app.adapter.EventEmitterAdapter;
import com.studiomediatech.queryresponse.ui.app.adapter.QueryPublisherAdapter;
import com.studiomediatech.queryresponse.ui.app.adapter.WebSocketApiAdapter;
import com.studiomediatech.queryresponse.ui.app.telemetry.NodeRepository;
import com.studiomediatech.queryresponse.ui.app.telemetry.TelemetryService;
import com.studiomediatech.queryresponse.ui.infra.repo.InMemoryNodeRepository;

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
    @ConditionalOnMissingBean
    NodeRepository inMemoryNodeRepository() {
        return new InMemoryNodeRepository();
    }

    @Bean
    public TelemetryService telemetryService(Optional<WebSocketApiAdapter> webSocketApiAdapter,
            NodeRepository nodeRepository, Optional<QueryPublisherAdapter> queryPublisherAdapter) {
        return new TelemetryService(webSocketApiAdapter.orElse(WebSocketApiAdapter.empty()), nodeRepository,
                queryPublisherAdapter.orElse(QueryPublisherAdapter.empty()));
    }
}
