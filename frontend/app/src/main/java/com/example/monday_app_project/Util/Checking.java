package com.example.monday_app_project.Util;

import android.util.Patterns;
public class Checking {
    public static boolean IsValidEmail(String email)
    {
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
