package com.worthybitbuilders.squadsense.models;

public class ActivityLog {
    public static class ActivityLogCreator {
        public String _id;
        public String name;
        public String profileImagePath;

        public ActivityLogCreator(String _id, String name, String profileImagePath) {
            this._id = _id;
            this.name = name;
            this.profileImagePath = profileImagePath;
        }
    }

    public static class ActivityLogProject {

        public String _id;
        public String title;

        public ActivityLogProject(String _id, String title) {
            this._id = _id;
            this.title = title;
        }
    }

    public static class ActivityLogBoard {

        public String _id;
        public String boardTitle;

        public ActivityLogBoard(String _id, String boardTitle) {
            this._id = _id;
            this.boardTitle = boardTitle;
        }
    }

    private final ActivityLogCreator creator;
    private final ActivityLogProject project;
    private final ActivityLogBoard board;
    private final String cellId;
    private final String description;
    /** Four types: New, Update, Change, Remove */
    private final String type;
    private final String createdAt;

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
