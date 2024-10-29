package com.worthybitbuilders.squadsense.models.board_models;

import com.google.gson.annotations.SerializedName;

public class BoardColumnHeaderModel {
    public enum ColumnType {
        @SerializedName("Status")
        Status(1, "Status"),
        @SerializedName("Update")
        Update(2, "Update"),
        @SerializedName("Text")
        Text(3, "Text"),
        @SerializedName("Number")
        Number(4, "Number"),
        @SerializedName("Timeline")
        TimeLine(5, "Timeline"),
        @SerializedName("Date")
        Date(6, "Date"),
        @SerializedName("User")
        User(7, "User"),
        @SerializedName("Checkbox")
        Checkbox(8, "Checkbox"),
        NewColumn(9, "+ New Column");

        private int key;
        private String name;
        ColumnType(int key, String name) {
            this.key = key;
            this.name = name;
        }

        public int getKey() {
            return key;
        }
        public String getName() { return name; }
    }
    private ColumnType columnType;
    private String title;
    private String description;

    public BoardColumnHeaderModel(ColumnType columnType, String title) {
        this.columnType = columnType;
        this.title = title;
        this.description = "";
    }

    public BoardColumnHeaderModel(ColumnType columnType, String title, String description) {
        this.columnType = columnType;
        this.title = title;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ColumnType getColumnType() {
        return columnType;
    }

    public void setColumnType(ColumnType columnType) {
        this.columnType = columnType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
