package com.worthybitbuilders.squadsense.models.report_models;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class StatusReportModel {
    public String title;
    public ArrayList<StatusReportItemModel> statuses;

    public StatusReportModel(String title, ArrayList<StatusReportItemModel> status){
        this.title = title;
        this.statuses = status;
    }
}
