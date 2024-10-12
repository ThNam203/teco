package com.example.monday_app_project.ViewModels;

import android.util.Patterns;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;

public class LoginViewModel extends ViewModel {
    private MutableLiveData<HashMap<String, String>> LoginUserListLiveData = new MutableLiveData<>();
    private HashMap<String, String> LoginUserList;

    public LoginViewModel()
    {
        LoginUserList = new HashMap<>();
        LoginUserList.put("abc@gmail.com", "123");
        LoginUserListLiveData.setValue(LoginUserList);
    }

    public boolean AddNewLoginUser(String email, String password)
    {
        LoginUserList = LoginUserListLiveData.getValue();
        if(LoginUserList == null)
        {
            LoginUserList = new HashMap<>();
        }
        if(IsEmailExisted(email))
            return false;

        LoginUserList.put(email, password);
        LoginUserListLiveData.setValue(LoginUserList);
        return true;
    }

    public boolean IsEmailExisted(String email)
    {
        if(LoginUserList.get(email) != null)
            return true;
        return false;
    }

    public boolean IsValidEmail(String email)
    {
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean IsLoginSuccess(String email, String password)
    {
        if(LoginUserList.get(email).equals(password))
            return true;
        return false;
    }

}
