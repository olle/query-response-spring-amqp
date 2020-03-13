package com.studiomediatech.queryresponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Trait for logging, with convenient access to a default instance based logger.
 */
public interface Logging {


    default Logger log() {

        return LoggerFactory.getLogger(getClass());
    }
}
