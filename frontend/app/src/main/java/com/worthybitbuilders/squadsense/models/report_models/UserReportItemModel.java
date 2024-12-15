package com.worthybitbuilders.squadsense.models.report_models;

public class UserReportItemModel {
    public String assigned;
    public String unassigned;
    public UserReportItemModel(String assigned, String unassigned) {
        this.assigned = assigned;
        this.unassigned = unassigned;
    }
}
