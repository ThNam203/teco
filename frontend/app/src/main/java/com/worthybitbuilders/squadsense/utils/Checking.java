package com.worthybitbuilders.squadsense.utils;

import android.util.Patterns;
public class Checking {
    public static boolean IsValidEmail(String email)
    {
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
