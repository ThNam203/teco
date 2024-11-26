package com.worthybitbuilders.squadsense.models.report_models;

public class TimelineModel {
    public String title;
    public TimelineValuesModel values;

    // Constructor
    public TimelineModel(String title, TimelineValuesModel values) {
        this.title = title;
        this.values = values;
    }

    // Nested Values class
    public static class TimelineValuesModel {
        public int before;
        public int during;
        public int after;
        public int undefinedValue;

        // Constructor
        public TimelineValuesModel(int before, int during, int after, int undefinedValue) {
            this.before = before;
            this.during = during;
            this.after = after;
            this.undefinedValue = undefinedValue;
        }
    }
}
