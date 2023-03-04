package com.studiomediatech.queryresponse.util;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class DurationFormatterTest {

    @Test
    void millis() {

        Duration d = Duration.ofMillis(999);
        assertThat(DurationFormatter.format(d)).isEqualTo("0 seconds");
    }

    @Test
    void second() throws Exception {

        Duration d = Duration.ofSeconds(1);
        assertThat(DurationFormatter.format(d)).isEqualTo("1 second");
    }

    @Test
    void seconds() throws Exception {

        Duration d = Duration.ofSeconds(2);
        assertThat(DurationFormatter.format(d)).isEqualTo("2 seconds");
    }

    @Test
    void minute() throws Exception {

        Duration d = Duration.ofSeconds(119);
        assertThat(DurationFormatter.format(d)).isEqualTo("1 minute");
    }

    @Test
    void minutes() throws Exception {

        Duration d = Duration.ofMinutes(23);
        assertThat(DurationFormatter.format(d)).isEqualTo("23 minutes");
    }

    @Test
    void hour() throws Exception {

        Duration d = Duration.ofHours(1).plusMinutes(12);
        assertThat(DurationFormatter.format(d)).isEqualTo("1 hour 12min");
    }

    @Test
    void hours() throws Exception {

        Duration d = Duration.ofHours(4).plusMinutes(33);
        assertThat(DurationFormatter.format(d)).isEqualTo("4 hours 33min");
    }

    @Test
    void onlyHours() throws Exception {

        Duration d = Duration.ofHours(12);
        assertThat(DurationFormatter.format(d)).isEqualTo("12 hours");
    }

    @Test
    void day() throws Exception {

        Duration d = Duration.ofDays(1).plusHours(3);
        assertThat(DurationFormatter.format(d)).isEqualTo("1 day 3h");
    }

    @Test
    void days() throws Exception {

        Duration d = Duration.ofDays(32).plusHours(1);
        assertThat(DurationFormatter.format(d)).isEqualTo("32 days 1h");
    }

    @Test
    void onlyDays() throws Exception {

        Duration d = Duration.ofDays(77);
        assertThat(DurationFormatter.format(d)).isEqualTo("77 days");
    }

    @Test
    void year() throws Exception {

        Duration d = Duration.ofDays(356).plusDays(1);
        assertThat(DurationFormatter.format(d)).isEqualTo("1 year 1d");
    }

    @Test
    void years() throws Exception {

        Duration d = Duration.ofDays(356 * 3).plusDays(44);
        assertThat(DurationFormatter.format(d)).isEqualTo("3 years 44d");
    }

    @Test
    void onlyYears() throws Exception {

        Duration d = Duration.ofDays(356 * 2);
        assertThat(DurationFormatter.format(d)).isEqualTo("2 years");
    }
}
