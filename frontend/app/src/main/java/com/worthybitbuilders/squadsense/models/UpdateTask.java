package com.worthybitbuilders.squadsense.models;

import java.util.List;

public class UpdateTask {
    private String authorId;
    private transient String authorName;
    private transient String authorImagePath;
    private String content;
    private transient List<String> files;
    private transient String createdAt;

    public UpdateTask(String authorId, String content) {
        this.authorId = authorId;
        this.content = content;
    }

    public UpdateTask(String authorId, String authorName, String authorImagePath, String content, List<String> files, String createdAt) {
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorImagePath = authorImagePath;
        this.content = content;
        this.files = files;
        this.createdAt = createdAt;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorImagePath() {
        return authorImagePath;
    }

    public void setAuthorImagePath(String authorImagePath) {
        this.authorImagePath = authorImagePath;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}