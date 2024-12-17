package com.worthybitbuilders.squadsense.models.report_models;

public class CheckBox {
    public String title;
    public int checked;
    public int unchecked;

    // Constructor
    public CheckBox(String title, int checked, int unchecked) {
        this.title = title;
        this.checked = checked;
        this.unchecked = unchecked;
    }
}
