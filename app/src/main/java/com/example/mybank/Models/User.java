package com.example.mybank.Models;

public class User {
    private int _id;
    private String email;
    private String password;
    private String first_name;
    private String last_name;
    private String image_url;
    private double remained_amount;
    private String address;

    public User(int _id, String email, String password, String first_name, String last_name, String image_url, double remained_amount, String address) {
        this._id = _id;
        this.email = email;
        this.password = password;
        this.first_name = first_name;
        this.last_name = last_name;
        this.image_url = image_url;
        this.remained_amount = remained_amount;
        this.address = address;
    }
    public User()
    {

    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public double getRemained_amount() {
        return remained_amount;
    }

    public void setRemained_amount(double remained_amount) {
        this.remained_amount = remained_amount;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "User{" +
                "_id=" + _id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", image_url='" + image_url + '\'' +
                ", remained_amount=" + remained_amount +
                ", address='" + address + '\'' +
                '}';
    }
}
