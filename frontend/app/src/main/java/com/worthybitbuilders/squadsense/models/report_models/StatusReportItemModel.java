package com.worthybitbuilders.squadsense.models.report_models;

public class StatusReportItemModel {
    public String label;
    public String count;

    public String color;

    public StatusReportItemModel(String label, String count, String color){
        this.label = label;
        this.count = count;
        this.color = color;
    }

    public StatusReportItemModel(String label, String count) {
        this(label, count, "#FFF");
    }
}
