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

    public StatsProperties getStats() {
		return stats;
	}

	public void setStats(StatsProperties stats) {
		this.stats = stats;
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


    /**
     * Encapsulates the settings for the statistics and metrics gathering.
     */
    public static class StatsProperties {
    	
    	/**
    	 * Topic or routing-key for statistics messages.
    	 */
    	private String topic = "query-response/internal/stats";
    	
    	/**
    	 * Initial delay, before the first published statistics after startup, in milliseconds.
    	 */
		private long initialDelay = 7000L;

		/**
		 * Delay between each publishing of statistics, in milliseconds.
		 */
		private long delay = 11000L;

		/**
		 * Retrieves the configured topic routing key, by default {@value StatsProperties#topic}.
		 * 
		 * @return the topic string
		 */
		public String getTopic() {
			return topic;
		}

		/**
		 * Sets the topic routing key for statistics messages.
		 * 
		 * @param topic routing key string, not empty or {@code null}.
		 */
		public void setTopic(String topic) {
			Assert.isTrue(StringUtils.hasText(topic), "Topic may not be empty");
			this.topic = topic;
		}

		/**
		 * Sets the initial delay before statistics and metrics messaging is to begin.
		 * 
		 * @param initialDelay in milliseconds
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
		 * Sets the delay used in gathering and publishing of metrics and statistics, by default {@value StatsProperties#delay}.
		 * 
		 * @param delay in milliseconds
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
