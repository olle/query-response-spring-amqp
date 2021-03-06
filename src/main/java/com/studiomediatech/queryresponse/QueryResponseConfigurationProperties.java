package com.studiomediatech.queryresponse;

import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * Configuration properties for Query/Response.
 */
@ConfigurationProperties(prefix = "queryresponse")
public class QueryResponseConfigurationProperties {

    private ExchangeProperties exchange = new ExchangeProperties();

    private QueueProperties queue = new QueueProperties();

    public ExchangeProperties getExchange() {

        return exchange;
    }


    public void setExchange(ExchangeProperties exchange) {

        this.exchange = exchange;
    }


    public QueueProperties getQueue() {

        return queue;
    }


    public void setQueue(QueueProperties queue) {

        this.queue = queue;
    }

    public static class ExchangeProperties {

        /**
         * Name of the shared topic exchange for queries.
         */
        private String name = "query-response";

        public String getName() {

            return name;
        }


        public void setName(String name) {

            this.name = name;
        }
    }

    public static class QueueProperties {

        /**
         * Prefix for queue names.
         */
        private String prefix = "query-response-";

        public String getPrefix() {

            return prefix;
        }


        public void setPrefix(String prefix) {

            this.prefix = prefix;
        }
    }
}
