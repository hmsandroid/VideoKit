package com.videokitnative.huawei.utils;

import java.util.Locale;

/**
 * Time util
 */
public class TimeUtil {
    /**
     * Ms to second
     */
    public static final int MS_TO_SECOND = 1000;

    /**
     * Second to minute
     */
    private static final int SECOND_TO_MINUTE = 60;

    /**
     * Second to hour
     */
    private static final int SECOND_TO_HOUR = 60 * 60;

    /**
     * ms to 00:00:00
     *
     * @param time ms
     * @return String
     */
    public static String formatLongToTimeStr(int time) {
        int totalSeconds = time / MS_TO_SECOND;
        int seconds = totalSeconds % SECOND_TO_MINUTE;
        int minutes = totalSeconds / SECOND_TO_MINUTE;
        int hours = totalSeconds / SECOND_TO_HOUR;

        if (hours > 0) {
            minutes %= SECOND_TO_MINUTE;
            return String.format(Locale.ENGLISH, "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds);
        }
    }
}


