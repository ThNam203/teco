package com.example.monday_app_project.Pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.monday_app_project.MainActivity;
import com.example.monday_app_project.R;
import com.example.monday_app_project.Util.SwitchActivity;
import com.example.monday_app_project.ViewModels.LoginViewModel;
import com.example.monday_app_project.ViewModels.TaskBoardViewModel;

public class page_login extends AppCompatActivity {

    EditText loginEmail;
    EditText loginPassword;
    AppCompatButton btnNext;

    LoginViewModel loginViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_login);
        getSupportActionBar().hide();

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        loginEmail = (EditText) findViewById(R.id.login_email);
        loginPassword = (EditText) findViewById(R.id.login_password);
        btnNext = (AppCompatButton) findViewById(R.id.btn_next);
        btnNext.setEnabled(false);

        loginEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence content, int i, int i1, int i2) {
                String inputEmail = content.toString();
                if(loginViewModel.IsValidEmail(inputEmail))
                {
                    int color = ResourcesCompat.getColor(getResources(), R.color.btn_enabled_color, null);
                    Drawable drawable = btnNext.getBackground();
                    drawable.setTint(color);
                    btnNext.setBackground(drawable);
                    btnNext.setEnabled(true);
                }
                else
                {
                    int color = ResourcesCompat.getColor(getResources(), R.color.btn_disabled_color, null);
                    Drawable drawable = btnNext.getBackground();
                    drawable.setTint(color);
                    btnNext.setBackground(drawable);
                    btnNext.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputEmail = loginEmail.getText().toString();
                String inputPassword = loginPassword.getText().toString();
                if(!loginViewModel.IsValidEmail(inputEmail))
                {
                    Toast.makeText(page_login.this, "Invalid email", Toast.LENGTH_SHORT).show();
                }
                else if (!loginViewModel.IsEmailExisted(inputEmail))
                {
                    Toast.makeText(page_login.this, "Not exist this email", Toast.LENGTH_SHORT).show();
                }
                else if (loginViewModel.IsLoginSuccess(inputEmail,inputPassword))
                {
                    SwitchActivity.switchToActivity(getWindow().getContext(), MainActivity.class);
                }
                else
                {
                    Toast.makeText(page_login.this, "Wrong password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}