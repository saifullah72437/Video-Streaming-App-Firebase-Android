package com.saif.videostreamingapp;

public class UsersModel {
    String name, email, password, imagePath;

    public UsersModel() {
    }

    public UsersModel(String name, String email, String password, String imagePath) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
