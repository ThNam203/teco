package com.example.monday_app_project.Models;

import android.util.Patterns;

import com.example.monday_app_project.Util.Checking;

public class UserModel {
    private String email;
    private String password;


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
}
