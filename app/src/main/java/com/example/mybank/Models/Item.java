package com.example.mybank.Models;

public class Item {
    private int _id;
    private String name;
    private String description;

    public Item(int _id, String name, String description) {
        this._id = _id;
        this.name = name;
        this.description = description;
    }

    public Item() {
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

    @Override
    public String toString() {
        return "Item{" +
                "_id=" + _id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
