package com.studiomediatech.queryresponse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.amqp.rabbit.junit.RabbitAvailable;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;


@RabbitAvailable
public class StatisticsBeanConfigurationTest {

    ApplicationContextRunner contextRunner =
        new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(QueryResponseConfiguration.class,
                RabbitAutoConfiguration.class));

    @Test
    @DisplayName("a statistics component bean is wired by the configuration")
    void ensureWiresStatisticsComponent() throws Exception {

        contextRunner.run(ctx -> assertThat(ctx.getBean(Statistics.class)).isNotNull());
    }
}
