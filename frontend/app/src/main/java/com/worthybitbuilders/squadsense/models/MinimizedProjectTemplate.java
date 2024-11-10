package com.worthybitbuilders.squadsense.models;

public class MinimizedProjectTemplate {
    private String title;
    private String content;
    private int color;

    public enum Type {IT_MANAGEMENT, EVENT_MANAGEMENT}
    private Type type;
    public MinimizedProjectTemplate(String title, String content, int color, Type type) {
        this.title = title;
        this.content = content;
        this.color = color;
        this.type = type;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getColor() {
        return color;
    }
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
