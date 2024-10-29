package com.worthybitbuilders.squadsense.utils;

import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CustomUtils {
    // ALERT ---- MONTH START FROM 0 TO 11
    public static String convertIntToMonth(int month) {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        if (month < 0 || month > 11) {
            throw new IllegalArgumentException("Invalid month value. Must be between 1 and 12.");
        }

        return months[month];
    }


    // UTIL FOR TIMELINE ITEM
    public static long getTimeInMillis(int day, int month, int year) {
        if (year == -1 || month == -1 || day == -1) {
            return MaterialDatePicker.todayInUtcMilliseconds();
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String dateString = String.format(Locale.US, "%04d/%02d/%02d", year, month, day);
            LocalDate localDateTime = LocalDate.parse(dateString,
                    DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            return localDateTime
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant().toEpochMilli() + 86400000L;
        } else {
            String startDate = String.format(Locale.US, "%04d/%02d/%02d", year, month, day);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
            try {
                Date date = sdf.parse(startDate);
                return date.getTime();
            } catch (ParseException e) {
                throw  new NullPointerException();
            }
        }
    }

    public static String mongooseDateToFormattedString(String mongooseDate) {

        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        Date formattedDate;
        try {
            formattedDate = sdf.parse(mongooseDate);
        } catch (ParseException e) {
            throw new RuntimeException();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(formattedDate);
        return String.format(Locale.US, "%s %d, %d", CustomUtils.convertIntToMonth(calendar.get(Calendar.MONTH)),calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR));
    }
}
