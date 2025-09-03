package com.example.todo;

public class Note {
    private int id;
    private String title;
    private String description;
    private String date; // new field

    // Constructor for new note (without id)
    public Note(String title, String description, String date) {
        this.title = title;
        this.description = description;
        this.date = date;
    }

    // Constructor with id (for reading from DB)
    public Note(int id, String title, String description, String date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
    }

    // Getters and Setters
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
