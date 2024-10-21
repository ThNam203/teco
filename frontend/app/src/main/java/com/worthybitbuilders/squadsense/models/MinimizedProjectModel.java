package com.worthybitbuilders.squadsense.models;

/**
 * This class is for the home fragment projects list
 */
public class MinimizedProjectModel {
    private String _id;
    private String title;
    private String updatedAt;

    public MinimizedProjectModel(String _id, String title, String updatedAt) {
        this._id = _id;
        this.title = title;
        this.updatedAt = updatedAt;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
