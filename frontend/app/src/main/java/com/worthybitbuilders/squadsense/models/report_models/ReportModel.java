package com.worthybitbuilders.squadsense.models.report_models;

import java.util.List;

public class ReportModel {
    public String title;
    public List<CheckBox> checkbox;
    public List<TimelineModel> timeline;
    public List<StatusReportModel> status;

    public ReportModel(String title,List<CheckBox> checkbox, List<TimelineModel> timeline, List<StatusReportModel> status){
        this.title = title;
        this.checkbox = checkbox;
        this.timeline = timeline;
        this.status = status;
    }

}
