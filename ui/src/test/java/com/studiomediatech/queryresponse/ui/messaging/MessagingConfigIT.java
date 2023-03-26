package com.studiomediatech.queryresponse.ui.messaging;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(classes = MessagingConfig.class)
class MessagingConfigIT {

    @Autowired
    ApplicationContext ctx;

    @Test
    void ensure_has_telemetry_queue_with_binding() {

        Queue queue = (Queue) ctx.getBean(MessagingConfig.QUERY_RESPONSE_STATS_QUEUE_BEAN);
        assertThat(queue).isNotNull();

        Binding binding = (Binding) ctx.getBean(MessagingConfig.QUERY_RESPONSE_STATS_QUEUE_BINDING_BEAN);
        assertThat(binding).isNotNull();
        assertThat(binding.getDestination()).isEqualTo(queue.getName());
        assertThat(binding.getRoutingKey()).isEqualTo("query-response/internal/stats");
        assertThat(binding.getExchange()).isEqualTo("query-response");
    }
}
