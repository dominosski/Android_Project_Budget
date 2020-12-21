package com.example.mybank.Models;

public class Project {
    private int _id;
    private String name;
    private double initialBudget;
    private String initDate;
    private String finishDate;

    public Project(int _id, double initialBudget, String initDate, String finishDate, String name) {
        this._id = _id;
        this.initialBudget = initialBudget;
        this.initDate = initDate;
        this.finishDate = finishDate;
        this.name = name;
    }

    public Project() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public double getInitialBudget() {
        return initialBudget;
    }

    public void setInitialBudget(double initialBudget) {
        this.initialBudget = initialBudget;
    }

    public String getInitDate() {
        return initDate;
    }

    public void setInitDate(String initDate) {
        this.initDate = initDate;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    @Override
    public String toString() {
        return "Project{" +
                "_id=" + _id +
                ", name='" + name + '\'' +
                ", initialBudget=" + initialBudget +
                ", initDate='" + initDate + '\'' +
                ", finishDate='" + finishDate + '\'' +
                '}';
    }
}
