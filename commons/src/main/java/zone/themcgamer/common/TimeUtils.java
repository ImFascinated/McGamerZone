package zone.themcgamer.common;

import java.text.SimpleDateFormat;

/**
 * @author Braydon
 */
public class TimeUtils {
    /**
     * Format the given time as a date and time {@link String}
     * @param time the time to format
     * @return the formatted time
     */
    public static String when(long time) {
        return new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(time);
    }

    public static String formatIntoDetailedString(long time, boolean shortTime) {
        int secs = (int) (time / 1000L);
        if (secs == 0)
            return "0 " + (shortTime ? "s" : "seconds");
        int remainder = secs % 86400;
        int days = secs / 86400;
        int hours = remainder / 3600;
        int minutes = remainder / 60 - hours * 60;
        int seconds = remainder % 3600 - minutes * 60;
        String fDays = (days > 0) ? (" " + days + (shortTime ? "d" : " day") + ((days > 1) ? shortTime ? "" : "s" : "")) : "";
        String fHours = (hours > 0) ? (" " + hours + (shortTime ? "h" : " hour") + ((hours > 1) ? shortTime ? "" : "s" : "")) : "";
        String fMinutes = (minutes > 0) ? (" " + minutes + (shortTime ? "m" : " minute") + ((minutes > 1) ? shortTime ? "" : "s" : "")) : "";
        String fSeconds = (seconds > 0) ? (" " + seconds + (shortTime ? "s" : " second") + ((seconds > 1) ? shortTime ? "" : "s" : "")) : "";
        return (fDays + fHours + fMinutes + fSeconds).trim();
    }

    /**
     * Convert the provided unix time into readable time such as "1.0 Minute"
     * @param time - The unix time to convert
     * @return the formatted time
     */
    public static String convertString(long time) {
        return convertString(time, true, TimeUnit.FIT, false);
    }

    /**
     * Convert the provided unix time into readable time such as "1.0 Minute"
     * @param time - The unix time to convert
     * @param includeDecimals - Whether or not to format the time with decimals
     * @param type - The type to format the time as. Use {@code TimeUnit.FIT} for
     *               the time to format based on the given unix time
     * @param shortString - Whether or not the time string is shortened
     * @return the formatted time
     */
    public static String convertString(long time, boolean includeDecimals, TimeUnit type, boolean shortString) {
        if (time == -1L)
            return "Perm" + (shortString ? "" : "anent");
        else if (time <= 0L)
            return "0.0" + (shortString ? "ms" : " Millisecond");
        if (type == TimeUnit.FIT) {
            if (time < java.util.concurrent.TimeUnit.MINUTES.toMillis(1L))
                type = TimeUnit.SECONDS;
            else if (time < java.util.concurrent.TimeUnit.HOURS.toMillis(1L))
                type = TimeUnit.MINUTES;
            else if (time < java.util.concurrent.TimeUnit.DAYS.toMillis(1L))
                type = TimeUnit.HOURS;
            else type = TimeUnit.DAYS;
        }
        double num;
        String text;
        if (type == TimeUnit.DAYS) {
            num = MathUtils.format(time / 8.64E7, 1);
            text = shortString ? "d" : " Day";
        } else if (type == TimeUnit.HOURS) {
            num = MathUtils.format(time / 3600000.0, 1);
            text = shortString ? "h" : " Hour";
        } else if (type == TimeUnit.MINUTES) {
            num = MathUtils.format(time / 60000.0, 1);
            text = shortString ? "m" : " Minute";
        } else if (type == TimeUnit.SECONDS) {
            num = MathUtils.format(time / 1000.0, 1);
            text = shortString ? "s" : " Second";
        } else {
            num = MathUtils.format(time, 1);
            text = shortString ? "ms" : " Millisecond";
        }
        if (includeDecimals)
            text = num + text;
        else text = ((int) num) + text;
        if (num != 1.0 && !shortString)
            text+= "s";
        return text;
    }

    public enum TimeUnit {
        FIT, DAYS, HOURS, MINUTES, SECONDS, MILLISECONDS
    }
}