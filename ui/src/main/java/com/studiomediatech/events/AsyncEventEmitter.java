package com.studiomediatech.events;

import org.springframework.context.ApplicationEventPublisher;

import org.springframework.scheduling.TaskScheduler;

import java.time.Instant;

/**
 * An implementation that ensures decoupling from the calling thread.
 */
public class AsyncEventEmitter implements EventEmitter {

    private final TaskScheduler scheduler;
    private final ApplicationEventPublisher publisher;

    public AsyncEventEmitter(TaskScheduler scheduler, ApplicationEventPublisher publisher) {

        this.scheduler = scheduler;
        this.publisher = publisher;
    }

    @Override
    public void emitEvent(Object event) {

        // NOTE: Scheduled ASAP for any instant in the past.
        Instant asap = Instant.EPOCH;

        scheduler.schedule(() -> publisher.publishEvent(event), asap);
    }
}
