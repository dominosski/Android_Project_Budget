package com.example.mybank.Models;

public class Note {

    private int _id;
    private String name;
    private String date;
    private String description;
    private String priority;
    private int user_id;

    public Note(int _id, String name, String date, String description, int user_id, String priority) {
        this._id = _id;
        this.name = name;
        this.date = date;
        this.description = description;
        this.user_id = user_id;
        this.priority = priority;
    }

    public Note() {
    }

    @Override
    public String toString() {
        return "Note{" +
                "_id=" + _id +
                ", name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", description='" + description + '\'' +
                ", priority='" + priority + '\'' +
                ", user_id=" + user_id +
                '}';
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
