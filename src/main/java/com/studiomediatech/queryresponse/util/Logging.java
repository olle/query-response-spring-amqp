package com.studiomediatech.queryresponse.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Declarable;


/**
 * Trait for logging, with convenient access to a default instance based logger.
 */
public interface Logging {

	/**
	 * Provides a default logger
	 * 
	 * @return a logger instance, never {@code null}
	 */
    default Logger log() {

        return LoggerFactory.getLogger(getClass());
    }


    /**
     * Logs about the provided declarable, in a pre-formatted manner.
     * 
     * @param <T> declarable type
     * @param obj the declarable object parameter
     * 
     * @return the current logger, for chaining
     */
    default <T extends Declarable> T log(T obj) {

        log().info("//> Declared {}", obj);

        return obj;
    }
}
