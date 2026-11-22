package com.studiomediatech.queryresponse.ui.app.adapter;

/**
 * Declares the contract provided to clients, on how an event can be emitted.
 */
@FunctionalInterface
public interface EventEmitterAdapter {
    void emitEvent(Object event);
}
