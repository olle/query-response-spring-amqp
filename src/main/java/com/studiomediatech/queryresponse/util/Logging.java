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
    
    /**
     * Logs a pre-formatted debug line for some published message, either with rich or redacted message information,
     * depending on the current logging level.
     * 
     * @param type of publish
     * @param routingKey that is published
     * @param message that is published
     */
	default void debugLogPublished(String type, String routingKey, Message message) {

		log().debug("|<-- Published {}: {} - {}", type, routingKey, toStringRedacted(message));
	}
	
	/**
	 * Logs a pre-formatted info or debug line, for some published message, either with rich or redacted message
	 * information, depending on the current logging level.
	 * 
	 * @param type of publish
	 * @param routingKey that is published
	 * @param message that is published
	 */
    default void logPublished(String type, String routingKey, Message message) {

        if (log().isTraceEnabled()) {
        	log().debug("|<-- Published {}: {} - {}", type, routingKey, message);
        } else {
            log().info("|<-- Published {}: {} - {}", type, routingKey, toStringRedacted(message));
        }
    }


    /**
     * Builds a string of redacted information from the given message.
     * 
     * @param message used to build the information from
     * 
     * @return a string of redacted information, never empty or {@code null}
     */
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
