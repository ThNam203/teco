package com.worthybitbuilders.squadsense.models;

import java.util.List;

public class UpdateTask {
    private class UpdateTaskAuthor {
        public String _id;
        public String name;
        public String email;
        public String profileImagePath;
        public UpdateTaskAuthor(String _id) {
            this._id = _id;
        }

        public UpdateTaskAuthor(String _id, String name, String profileImagePath) {
            this._id = _id;
            this.name = name;
            this.profileImagePath = profileImagePath;
        }
    }

    public class UpdateTaskFile {
        public String location;
        public String name;
        public String fileType;

        public UpdateTaskFile(String location, String name, String fileType) {
            this.location = location;
            this.name = name;
            this.fileType = fileType;
        }
    }

    private String _id;
    private UpdateTaskAuthor author;
    private String cellId;
    private String content;
    private List<UpdateTaskFile> files;
    private int likeCount;
    private boolean isLiked;
    private String createdAt;

    public UpdateTask(String authorId, String content) {
        this.author = new UpdateTaskAuthor(authorId);
        this.content = content;
    }

    public String get_id() {
        return _id;
    }

    public String getCellId() {
        return cellId;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public String getAuthorId() {
        return author._id;
    }

    public String getAuthorName() {
        return author.name;
    }
    public String getAuthorEmail() { return author. email; }

    public String getAuthorImagePath() {
        return author.profileImagePath;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<UpdateTaskFile> getFiles() {
        return files;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}