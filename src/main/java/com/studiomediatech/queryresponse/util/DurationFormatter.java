package com.studiomediatech.queryresponse.util;

import java.time.Duration;

/**
 * Provides utilities to format a {@link Duration java duration} as a human readable string. 
 */
public final class DurationFormatter {

    private static final int MILLIS_PER_SECOND = 1000;
    private static final int SECONDS_PER_MINUTE = 60;
    private static final int MINUTES_PER_HOUR = 60;
    private static final int HOURS_PER_DAY = 24;
    private static final int DAYS_PER_YEAR = 356;
    private static final int SECONDS_PER_DAY = 86400;

    private DurationFormatter() {

        // Hidden!
    }

    /**
     * Formats the given duration as a rounded human readable string. 
     * 
     * @param duration to format
     * 
     * @return a string representation of the duration, never {@code null} 
     */
    public static String format(Duration duration) {

        try {
            long millis = duration.toMillis();

            if (millis < MILLIS_PER_SECOND) {
                return "0 seconds";
            }
        } catch (ArithmeticException e) {
            // Too long duration, let's move on.
        }

        long seconds = duration.getSeconds();

        if (seconds < SECONDS_PER_MINUTE) {
            return seconds < 2 ? "1 second" : seconds + " seconds";
        }

        long minutes = duration.toMinutes();

        if (minutes < MINUTES_PER_HOUR) {
            return minutes < 2 ? "1 minute" : minutes + " minutes";
        }

        long hours = duration.toHours();

        if (hours < HOURS_PER_DAY) {
            return (hours < 2 ? "1 hour" : hours + " hours") + minutesUnlessZero(duration);
        }

        long days = duration.toDays();

        if (days < DAYS_PER_YEAR) {
            return (days < 2 ? "1 day" : days + " days") + hoursUnlessZero(duration);
        }

        long years = days / DAYS_PER_YEAR;

        return (years < 2 ? "1 year" : years + " years") + daysUnlessZero(duration);
    }


    private static String minutesUnlessZero(Duration duration) {

        int minutes = (int) (duration.toMinutes() % MINUTES_PER_HOUR);

        return minutes != 0 ? " " + minutes + "min" : "";
    }


    private static String hoursUnlessZero(Duration duration) {

        int hours = (int) (duration.toHours() % HOURS_PER_DAY);

        return hours != 0 ? " " + hours + "h" : "";
    }


    private static String daysUnlessZero(Duration duration) {

        long days = ((duration.getSeconds() / SECONDS_PER_DAY) % DAYS_PER_YEAR);

        return days != 0 ? " " + days + "d" : "";
    }
}
