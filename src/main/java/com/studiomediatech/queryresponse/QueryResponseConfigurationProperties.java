package com.studiomediatech.queryresponse;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Configuration properties for Query/Response.
 */
@ConfigurationProperties(prefix = "queryresponse")
public class QueryResponseConfigurationProperties {

    private ExchangeProperties exchange = new ExchangeProperties();
    private QueueProperties queue = new QueueProperties();
    private StatsProperties stats = new StatsProperties();

    /**
     * Creates a new Query/Response configuration properties instance.
     */
    public QueryResponseConfigurationProperties() {
    }

    /**
     * Retrieves the Query/Response exchange configuration.
     *
     * @return configuration properties bean
     */
    public ExchangeProperties getExchange() {
        return exchange;
    }

    /**
     * Sets the Query/Response exchange configuration.
     *
     * @param exchange
     *            configuration properties bean
     */
    public void setExchange(ExchangeProperties exchange) {
        this.exchange = exchange;
    }

    /**
     * Retrieves the Query/Response queue configuration.
     *
     * @return configuration properties bean
     */
    public QueueProperties getQueue() {
        return queue;
    }

    /**
     * Sets the Query/Response queue configuration.
     *
     * @param queue
     *            configuration properties bean
     */
    public void setQueue(QueueProperties queue) {
        this.queue = queue;
    }

    /**
     * Retrieves the Query/Response metrics and statistics configuration.
     *
     * @return configuration properties bean
     */
    public StatsProperties getStats() {
        return stats;
    }

    /**
     * Sets the Query/Response metrics and statistics configuration.
     *
     * @param stats
     *            configuration properties bean
     */
    public void setStats(StatsProperties stats) {
        this.stats = stats;
    }

    /**
     * Configuration properties that declares and defines the topic exchange resources to be used.
     */
    public static class ExchangeProperties {

        private static final String DEFAULT_QUERY_RESPONSE_TOPIC_EXCHANGE = "query-response";
        /**
         * Name of the shared topic exchange for queries.
         */
        private String name = DEFAULT_QUERY_RESPONSE_TOPIC_EXCHANGE;

        /**
         * Creates a new instance of exchange configuration properties for Query/Response.
         */
        public ExchangeProperties() {
        }

        /**
         * Retrieves the globally shared Query/Response topic exchange name.
         *
         * @return exchange name string
         */
        public String getName() {
            return name;
        }

        /**
         * Sets the globally shared Query/Response topic exchange name.
         *
         * NOTE: Changing this name will create a new distinct Query/Response network, where any other participating
         * modules will have to provide the same name configuration.
         *
         * @param name
         *            to use for the topic-exchange resource, never empty or {@code null}
         */
        public void setName(String name) {
            Assert.isTrue(StringUtils.hasText(name), "Topic exchange name can not be empty.");
            this.name = name;
        }
    }

    /**
     * Configuration properties that pertain to declared and generated queue resources.
     */
    public static class QueueProperties {

        private static final String DEFAULT_QUERY_RESPONSE_PREFIX = "query-response-";

        /**
         * Prefix for queue names.
         */
        private String prefix = DEFAULT_QUERY_RESPONSE_PREFIX;

        /**
         * Creates a new instance of queue properties for Query/Response.
         */
        public QueueProperties() {
        }

        /**
         * Retrieves the Query/Response naming prefix.
         *
         * @return prefix string
         */
        public String getPrefix() {
            return prefix;
        }

        /**
         * Sets the Query/Response prefix, used when naming messaging resources.
         *
         * @param prefix
         *            string to use, never empty or {@code null}.
         */
        public void setPrefix(String prefix) {
            Assert.isTrue(StringUtils.hasText(prefix), "Prefix must not be empty.");
            this.prefix = prefix;
        }
    }

    /**
     * Encapsulates the settings for the statistics and metrics gathering.
     */
    public static class StatsProperties {

        private static final long DEFAULT_INITIAL_DELAY = 7000L;
        private static final long DEFAULT_DELAY = 11000L;
        private static final String DEFAULT_QUERY_RESPONSE_INTERNAL_STATS_TOPIC = "query-response/internal/stats";

        /**
         * Topic or routing-key for statistics messages.
         */
        private String topic = DEFAULT_QUERY_RESPONSE_INTERNAL_STATS_TOPIC;

        /**
         * Initial delay, before the first published statistics after startup, in milliseconds.
         */
        private long initialDelay = DEFAULT_INITIAL_DELAY;

        /**
         * Delay between each publishing of statistics, in milliseconds.
         */
        private long delay = DEFAULT_DELAY;

        /**
         * Creates a new instance of statistics properties for Query/Response.
         */
        public StatsProperties() {
        }

        /**
         * Retrieves the configured topic routing key.
         *
         * @return the topic string
         */
        public String getTopic() {
            return topic;
        }

        /**
         * Sets the topic routing key for statistics messages.
         *
         * @param topic
         *            routing key string, not empty or {@code null}.
         */
        public void setTopic(String topic) {
            Assert.isTrue(StringUtils.hasText(topic), "Topic may not be empty");
            this.topic = topic;
        }

        /**
         * Sets the initial delay before statistics and metrics messaging is to begin.
         *
         * @param initialDelay
         *            in milliseconds
         */
        public void setInitialDelay(long initialDelay) {
            this.initialDelay = initialDelay;
        }

        /**
         * Retrieves the initial delay for the metrics and statistics messaging.
         *
         * @return milliseconds long
         */
        public long getInitialDelay() {
            return initialDelay;
        }

        /**
         * Sets the delay used in gathering and publishing of metrics and statistics.
         *
         * @param delay
         *            in milliseconds
         */
        public void setDelay(long delay) {
            this.delay = delay;
        }

        /**
         * Retrieves the delay delay (tempo) for metrics and statistics gathering and publishing.
         *
         * @return milliseconds long
         */
        public long getDelay() {
            return delay;
        }
    }
}
