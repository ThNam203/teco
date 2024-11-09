package com.worthybitbuilders.squadsense.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.worthybitbuilders.squadsense.MainActivity;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.databinding.ActivityFirstScreenBinding;
import com.worthybitbuilders.squadsense.utils.ActivityUtils;
import com.worthybitbuilders.squadsense.viewmodels.LoginViewModel;

public class FirstScreenActivity extends AppCompatActivity {
    ActivityFirstScreenBinding binding;
    LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityFirstScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Auto logging
        if (loginViewModel.isAutoLogging()) ActivityUtils.switchToActivity(FirstScreenActivity.this, MainActivity.class);

        binding.btnLogin.setOnClickListener(view -> {
            ActivityUtils.switchToActivity(FirstScreenActivity.this, LogInActivity.class);
        });

        binding.btnSignup.setOnClickListener(view -> {
            ActivityUtils.switchToActivity(FirstScreenActivity.this, SignUpActivity.class);
        });
    }
}