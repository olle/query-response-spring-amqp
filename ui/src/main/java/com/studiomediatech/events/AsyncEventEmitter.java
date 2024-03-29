package com.studiomediatech.events;

import org.springframework.context.ApplicationEventPublisher;

import org.springframework.scheduling.TaskScheduler;

import java.time.Instant;

/**
 * An implementation that emits as a new scheduled task, ensuring that the calling thread is no longer blocked.
 */
public class AsyncEventEmitter implements EventEmitter {

    // NOTE: Scheduled ASAP for any instant in the past.
    private static final Instant ASAP = Instant.EPOCH;

    private final TaskScheduler scheduler;
    private final ApplicationEventPublisher publisher;

    public AsyncEventEmitter(TaskScheduler scheduler, ApplicationEventPublisher publisher) {

        this.scheduler = scheduler;
        this.publisher = publisher;
    }

    @Override
    public void emitEvent(Object event) {

        scheduler.schedule(() -> publisher.publishEvent(event), ASAP);
    }
}
