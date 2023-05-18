package com.tomspencerlondon;

public class TaskListEntry {

    private String id;

    private String text;

    public TaskListEntry(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public TaskListEntry(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}