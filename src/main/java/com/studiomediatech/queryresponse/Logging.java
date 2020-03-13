package com.studiomediatech.queryresponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.core.Declarable;


/**
 * Trait for logging, with convenient access to a default instance based logger.
 */
public interface Logging {


    default Logger log() {

        return LoggerFactory.getLogger(getClass());
    }


    default <T extends Declarable> T log(T obj) {

        log().info("//> Declared {}", obj);

        return obj;
    }
}
