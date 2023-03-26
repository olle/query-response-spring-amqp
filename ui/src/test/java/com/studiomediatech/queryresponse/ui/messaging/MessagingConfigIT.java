package com.studiomediatech.queryresponse.ui.messaging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(classes = MessagingConfig.class)
class MessagingConfigIT {

    @Autowired
    ApplicationContext ctx;

    @Test
    void test() {
        String[] names = ctx.getBeanNamesForType(Queue.class);
        assertThat(names).contains(MessagingConfig.QUERY_RESPONSE_STATS_QUEUE_BEAN);
    }

}
