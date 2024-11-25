package com.worthybitbuilders.squadsense.models.report_models;

public class StatusReportItemModel {
    public String label;
    public String statusCount;

    public StatusReportItemModel(String label, String count){
        this.label = label;
        this.statusCount = count;
    }
}
