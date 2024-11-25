package com.worthybitbuilders.squadsense.models.report_models;

public class UserReportModel {

    public String title;
    public UserReportItemModel item;

    public UserReportModel(String title, UserReportItemModel item) {
        this.title = title;
        this.item = item;
    }
}
