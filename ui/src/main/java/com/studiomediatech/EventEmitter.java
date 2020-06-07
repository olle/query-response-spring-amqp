package com.studiomediatech;

@FunctionalInterface
public interface EventEmitter {

    void emitEvent(Object event);
}
