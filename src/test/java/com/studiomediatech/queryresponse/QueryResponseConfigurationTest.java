package com.studiomediatech.queryresponse;

import org.junit.jupiter.api.Test;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;


class QueryResponseConfigurationTest {

    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(QueryResponseConfiguration.class,
                RabbitAutoConfiguration.class));

    @Test
    void ensureConfiguresQueryRegistry() {

        contextRunner.run(ctx -> assertThat(ctx.getBean(QueryRegistry.class)).isNotNull());
    }


    @Test
    void ensureConfiguresResponseRegistry() throws Exception {

        contextRunner.run(ctx -> assertThat(ctx.getBean(ResponseRegistry.class)).isNotNull());
    }
}
