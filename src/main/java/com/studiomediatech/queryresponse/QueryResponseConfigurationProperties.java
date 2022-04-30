package com.studiomediatech.queryresponse;

import org.springframework.boot.context.properties.ConfigurationProperties;


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

		public String getTopic() {
			return topic;
		}

		public void setTopic(String topic) {
			this.topic = topic;
		}

		public void setInitialDelay(long initialDelay) {
			this.initialDelay = initialDelay;
		}
		
		public long getInitialDelay() {
			return initialDelay;
		}

		public void setDelay(long delay) {
			this.delay = delay;
		}
		
		public long getDelay() {
			return delay;
		}
    }
}
