package com.worthybitbuilders.squadsense.models.report_models;

import java.util.List;

public class UserReportModel {

    public String title;
    public List<Assignment> assigments;

    public UserReportModel(String title, List<Assignment> assignments) {
        this.title = title;
        this.assigments = assignments;
    }

    public static class Assignment {
        public String label;
        public List<String> assignee;
        public Assignment(String label, List<String> user) {
            this.label = label;
            this.assignee = user;
        }
    }

    public static class Assignee {
        public String name;

        public Assignee(String name) {
            this.name = name;
        }
    }
}
