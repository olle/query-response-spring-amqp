package com.studiomediatech.queryresponse;

import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * Configuration properties for Query/Response.
 */
@ConfigurationProperties(prefix = "queryresponse")
public class QueryResponseConfigurationProperties {

    private ExchangeProperties exchange = new ExchangeProperties();

    public ExchangeProperties getExchange() {

        return exchange;
    }


    public void setExchange(ExchangeProperties exchange) {

        this.exchange = exchange;
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
}
