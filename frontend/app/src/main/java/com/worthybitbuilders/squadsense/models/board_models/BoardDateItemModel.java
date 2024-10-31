package com.worthybitbuilders.squadsense.models.board_models;

import com.worthybitbuilders.squadsense.utils.CustomUtils;

import java.util.Locale;

public class BoardDateItemModel extends BoardBaseItemModel {
    // -1 means the value has not been initialized
    private int year = -1;
    private int month = -1;
    private int day = -1;
    private int hour = -1;
    private int minute = -1;
    public BoardDateItemModel() {
        super("", "CellDate");
    }

    public BoardDateItemModel(int year, int month, int day, int hour, int minute) {
        super("", "Date");
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

    public BoardDateItemModel(int year, int month, int day) {
        super("", "Date");
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public BoardDateItemModel(int hour, int minute) {
        super("", "Date");
        this.hour = hour;
        this.minute = minute;
    }

    @Override
    public String getContent() {
        String finalContent = "";
        if (year != -1 && month != - 1 && day != -1)
            finalContent += String.format(Locale.US,"%s %d, %d", CustomUtils.convertIntToMonth(month), day, year);
        if (hour != -1 && minute != -1) {
            if (finalContent.isEmpty())
                finalContent += String.format(Locale.US, "%02d:%02d", hour, minute);
            else finalContent += String.format(Locale.US, ", %02d:%02d", hour, minute);
        }
        return finalContent;
    }

    public String getDate() {
        if (year != -1 && month != - 1 && day != -1)
            return String.format(Locale.US,"%s %d, %d", CustomUtils.convertIntToMonth(month), day, year);
        return "";
    }


    public String getTime() {
        if (hour != -1 && minute != -1)
            return String.format(Locale.US, "%02d:%02d", hour, minute);
        return "";
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}
