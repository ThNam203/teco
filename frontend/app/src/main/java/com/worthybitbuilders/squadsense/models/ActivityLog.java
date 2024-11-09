package com.worthybitbuilders.squadsense.models;

public class ActivityLog {
    public class ActivityLogCreator {
        public String _id;
        public String name;
        public String profileImagePath;

        public ActivityLogCreator(String _id, String name, String profileImagePath) {
            this._id = _id;
            this.name = name;
            this.profileImagePath = profileImagePath;
        }
    }

    public class ActivityLogProject {

        public String _id;
        public String title;

        public ActivityLogProject(String _id, String title) {
            this._id = _id;
            this.title = title;
        }
    }

    public class ActivityLogBoard {

        public String _id;
        public String boardTitle;

        public ActivityLogBoard(String _id, String boardTitle) {
            this._id = _id;
            this.boardTitle = boardTitle;
        }
    }

    private ActivityLogCreator creator;
    private ActivityLogProject project;
    private ActivityLogBoard board;
    private String cellId;
    private String description;
    private String type;
    private String createdAt;

    public ActivityLog(ActivityLogCreator creator, ActivityLogProject project, ActivityLogBoard board, String cellId, String description, String type, String createdAt) {
        this.creator = creator;
        this.project = project;
        this.board = board;
        this.cellId = cellId;
        this.description = description;
        this.type = type;
        this.createdAt = createdAt;
    }

    public ActivityLogCreator getCreator() {
        return creator;
    }

    public ActivityLogProject getProject() {
        return project;
    }

    public ActivityLogBoard getBoard() {
        return board;
    }

    public String getCellId() {
        return cellId;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getCreatedDate() {
        return createdAt;
    }
}
