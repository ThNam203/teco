package com.worthybitbuilders.squadsense.models.report_models;

import java.util.List;

public class UserReportModel {

    public String title;
    public List<Assignment> assignments;

    public UserReportModel(String title, List<Assignment> assignments) {
        this.title = title;
        this.assignments = assignments;
    }

    public static class Assignment {
        public String label;
        public List<Assignee> user;
        public Assignment(String label, List<Assignee> user) {
            this.label = label;
            this.user = user;
        }
    }

    public static class Assignee {
        public String name;

        public Assignee(String name) {
            this.name = name;
        }
    }
}
