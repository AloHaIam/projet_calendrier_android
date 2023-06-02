package com.example.calendriervacances;

public class Event {
    private int id;
    private String title;
    private String description;
    private long date;

    public Event(int id, String title, String description, long date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public Event(String title, String description, long date) {
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
