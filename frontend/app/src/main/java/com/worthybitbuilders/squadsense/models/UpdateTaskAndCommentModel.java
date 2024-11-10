package com.worthybitbuilders.squadsense.models;

import com.worthybitbuilders.squadsense.viewmodels.UpdateTaskCommentViewModel;

import java.util.List;

public class UpdateTaskAndCommentModel {
    public class UpdateTaskCommentFile {
        public String location;
        public String name;
        public String fileType;

        public UpdateTaskCommentFile(String location, String name, String fileType) {
            this.location = location;
            this.name = name;
            this.fileType = fileType;
        }
    }
    public static class UpdateTaskComment {
        private String _id;
        private UpdateTask.UpdateTaskAuthor author;
        private String content;
        private List<UpdateTaskCommentFile> files;
        private boolean isLiked;
        private int likeCount;
        private String createdAt;

        public UpdateTaskComment(String content) {
            this.content = content;
        }

        public int getLikeCount() {
            return likeCount;
        }

        public void setLikeCount(int likeCount) {
            this.likeCount = likeCount;
        }

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public UpdateTask.UpdateTaskAuthor getAuthor() {
            return author;
        }

        public void setAuthor(UpdateTask.UpdateTaskAuthor author) {
            this.author = author;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public List<UpdateTaskCommentFile> getFiles() {
            return files;
        }

        public void setFiles(List<UpdateTaskCommentFile> files) {
            this.files = files;
        }

        public boolean isLiked() {
            return isLiked;
        }

        public void setLiked(boolean liked) {
            isLiked = liked;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }
    }

    UpdateTask updateTask;
    List<UpdateTaskComment> comments;

    public UpdateTaskAndCommentModel(UpdateTask updateTask, List<UpdateTaskComment> comments) {
        this.updateTask = updateTask;
        this.comments = comments;
    }

    public UpdateTask getUpdateTask() {
        return updateTask;
    }

    public List<UpdateTaskComment> getComments() {
        return comments;
    }
}
