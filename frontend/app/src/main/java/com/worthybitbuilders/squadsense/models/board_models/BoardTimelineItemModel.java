package com.worthybitbuilders.squadsense.models.board_models;

import com.worthybitbuilders.squadsense.utils.CustomUtils;

import java.util.Locale;

public class BoardTimelineItemModel extends BoardBaseItemModel {
    private int startYear = -1;
    private int startMonth = -1;
    private int startDay = -1;
    private int endYear = -1;
    private int endMonth = -1;
    private int endDay = -1;
    public BoardTimelineItemModel() {
        super("", "CellTimeline");
    }

    public BoardTimelineItemModel(int startYear, int startMonth, int startDay, int endYear, int endMonth, int endDay) {
        super("", "CellTimeline");
        this.startYear = startYear;
        this.startMonth = startMonth;
        this.startDay = startDay;
        this.endYear = endYear;
        this.endMonth = endMonth;
        this.endDay = endDay;
    }

    @Override
    public String getContent() {
        String finalContent = "";
        int startMonthToConvert = startMonth - 1;
        int endMonthToConvert = endMonth - 1;
        if (startDay != -1 && startMonth != -1 && startYear != -1) {
            if (startDay == endDay && startMonth == endMonth && startYear == endYear) {

            } else if (startMonth == endMonth && startYear == endYear)
                finalContent += String.format(Locale.US, "%d", startDay);
            else if (startYear == endYear) {
                finalContent += String.format(Locale.US, "%s %d", CustomUtils.convertIntToMonth(startMonthToConvert), startDay);
            } else finalContent += String.format(Locale.US, "%s %d, %d", CustomUtils.convertIntToMonth(startMonthToConvert), startDay, startYear);

            if (finalContent.isEmpty())
                finalContent += String.format(Locale.US, "%s %d, %d", CustomUtils.convertIntToMonth(endMonthToConvert), endDay, endYear);
            else finalContent += String.format(Locale.US, " - %s %d, %d", CustomUtils.convertIntToMonth(endMonthToConvert), endDay, endYear);
        }

        return finalContent;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public int getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(int startMonth) {
        this.startMonth = startMonth;
    }

    public int getStartDay() {
        return startDay;
    }

    public void setStartDay(int startDay) {
        this.startDay = startDay;
    }

    public int getEndYear() {
        return endYear;
    }

    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }

    public int getEndMonth() {
        return endMonth;
    }

    public void setEndMonth(int endMonth) {
        this.endMonth = endMonth;
    }

    public int getEndDay() {
        return endDay;
    }

    public void setEndDay(int endDay) {
        this.endDay = endDay;
    }
}
