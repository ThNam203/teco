package com.worthybitbuilders.squadsense.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CompareUtils {
    public static long getDateLeft(Date date)
    {
        Date currentDate = resetTimeToMidnight(new Date());
        Date tempDate = resetTimeToMidnight(date);
        // Calculate the days left between the two dates
        long timeDifference = tempDate.getTime() - currentDate.getTime();
        long daysLeft = TimeUnit.DAYS.convert(timeDifference, TimeUnit.MILLISECONDS);

        return daysLeft;
    }
    public static long[] getDateAndTimeLeft(Date date) {
        Date currentDate = new Date(); // Current date

        // Calculate the time difference between the two dates
        long timeDifference = date.getTime() - currentDate.getTime();

        // Calculate the days left
        long daysLeft = TimeUnit.DAYS.convert(timeDifference, TimeUnit.MILLISECONDS);

        // Calculate the time left (in milliseconds)
        long timeLeftInMillis = timeDifference % (24 * 60 * 60 * 1000); // Remainder after calculating days
        long hoursLeft = timeLeftInMillis / (60 * 60 * 1000); // Number of hours in the remaining time
        long minutesLeft = (timeLeftInMillis % (60 * 60 * 1000)) / (60 * 1000); // Number of minutes in the remaining time
        long secondsLeft = (timeLeftInMillis % (60 * 1000)) / 1000; // Number of seconds in the remaining time

        return new long[]{daysLeft, hoursLeft, minutesLeft, secondsLeft};
    }

    private static Date resetTimeToMidnight(Date date) {
        return new Date(date.getYear(), date.getMonth(), date.getDate());
    }
}
