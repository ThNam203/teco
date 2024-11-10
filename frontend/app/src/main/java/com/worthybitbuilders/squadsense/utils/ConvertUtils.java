package com.worthybitbuilders.squadsense.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConvertUtils {

    public enum Pattern {
        DAY_MONTH_YEAR, HOUR_MINUTE_SECOND, HOUR_MINUTE
    }
    public static String TimestampsToString(Timestamp timestamp, Pattern ePattern)
    {
        String pattern;
        switch (ePattern)
        {
            case DAY_MONTH_YEAR:
                pattern = "dd-MM-yyyy";
                break;
            case HOUR_MINUTE_SECOND:
                pattern = "HH:mm:ss";
                break;
            case HOUR_MINUTE:
                pattern = "HH:mm";
                break;
            default:
                pattern = "dd-MM-yyyy";
                break;
        }
        Date date = new Date(timestamp.getTime());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String dateFormat = simpleDateFormat.format(date);
        return dateFormat;
    }

    public static String DateToString(Date date, Pattern ePattern)
    {
        String pattern;
        switch (ePattern)
        {
            case DAY_MONTH_YEAR:
                pattern = "dd-MM-yyyy";
                break;
            case HOUR_MINUTE_SECOND:
                pattern = "HH:mm:ss";
                break;
            case HOUR_MINUTE:
                pattern = "HH:mm";
                break;
            default:
                pattern = "dd-MM-yyyy";
                break;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String dateFormat = simpleDateFormat.format(date);
        return dateFormat;
    }

    public static String DateToStringSecondary(Date date, Pattern ePattern)
    {
        String pattern;
        switch (ePattern)
        {
            case DAY_MONTH_YEAR:
                pattern = "dd/MM/yyyy";
                break;
            case HOUR_MINUTE_SECOND:
                pattern = "HH:mm:ss";
                break;
            case HOUR_MINUTE:
                pattern = "HH:mm";
                break;
            default:
                pattern = "dd/MM/yyyy";
                break;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String dateFormat = simpleDateFormat.format(date);
        return dateFormat;
    }

    public static String formatDate(int day, int month, int year)
    {
        String dateString = String.format("%02d-%02d-%d", day, month, year);
        return dateString;
    }
}
