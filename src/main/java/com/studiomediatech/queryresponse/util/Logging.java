package com.studiomediatech.queryresponse.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Declarable;
import org.springframework.amqp.core.Message;


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
    
	default void debugLogPublished(String type, String routingKey, Message message) {

		log().debug("|<-- Published {}: {} - {}", type, routingKey, toStringRedacted(message));
	}
	
    default void logPublished(String type, String routingKey, Message message) {

        if (log().isTraceEnabled()) {
        	log().trace("|<-- Published {}: {} - {}", type, routingKey, message);
        } else {
            log().debug("|<-- Published {}: {} - {}", type, routingKey, toStringRedacted(message));
        }
    }


    static String toStringRedacted(Message message) {

        StringBuilder buffer = new StringBuilder();
        buffer.append("(");
        buffer.append("Body:[").append(message.getBody().length).append("]");

        if (message.getMessageProperties() != null) {
            buffer.append(" ").append(message.getMessageProperties().toString());
        }

        buffer.append(")");

        return buffer.toString();
    }

}
