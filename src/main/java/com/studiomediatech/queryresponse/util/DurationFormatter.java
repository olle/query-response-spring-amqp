package com.studiomediatech.queryresponse.util;

import java.time.Duration;


public final class DurationFormatter {

    private DurationFormatter() {

        // Hidden!
    }

    public static String format(Duration duration) {

        try {
            long millis = duration.toMillis();

            if (millis < 1000) {
                return "0 seconds";
            }
        } catch (ArithmeticException e) {
            // Too long duration, let's move on.
        }

        long seconds = duration.toSeconds();

        if (seconds < 60) {
            return seconds < 2 ? "1 second" : seconds + " seconds";
        }

        long minutes = duration.toMinutes();

        if (minutes < 60) {
            return minutes < 2 ? "1 minute" : minutes + " minutes";
        }

        long hours = duration.toHours();

        if (hours < 24) {
            return (hours < 2 ? "1 hour" : hours + " hours") + minutesUnlessZero(duration);
        }

        long days = duration.toDays();

        if (days < 356) {
            return (days < 2 ? "1 day" : days + " days") + hoursUnlessZero(duration);
        }

        long years = days / 356;

        return (years < 2 ? "1 year" : years + " years") + daysUnlessZero(duration);
    }


    private static String minutesUnlessZero(Duration duration) {

        int minutes = duration.toMinutesPart();

        return minutes != 0 ? " " + minutes + "min" : "";
    }


    private static String hoursUnlessZero(Duration duration) {

        int hours = duration.toHoursPart();

        return hours != 0 ? " " + hours + "h" : "";
    }


    private static String daysUnlessZero(Duration duration) {

        long days = (duration.toDaysPart() % 356);

        return days != 0 ? " " + days + "d" : "";
    }
}
