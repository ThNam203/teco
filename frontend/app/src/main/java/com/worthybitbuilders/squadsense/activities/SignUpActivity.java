package com.worthybitbuilders.squadsense.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.databinding.ActivitySignUpBinding;
import com.worthybitbuilders.squadsense.models.UserModel;
import com.worthybitbuilders.squadsense.utils.ActivityUtils;
import com.worthybitbuilders.squadsense.utils.ToastUtils;
import com.worthybitbuilders.squadsense.viewmodels.SignUpViewModel;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SignUpActivity extends AppCompatActivity {
    private SignUpViewModel viewModel;
    private ActivitySignUpBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        getSupportActionBar().hide();
        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        binding.etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String inputEmail = charSequence.toString();
                if(viewModel.IsValidEmail(inputEmail))
                {
                    int color = ResourcesCompat.getColor(getResources(), R.color.btn_enabled_color, null);
                    Drawable drawable = binding.btnNext.getBackground();
                    drawable.setTint(color);
                    binding.btnNext.setBackground(drawable);
                    binding.btnNext.setEnabled(true);
                }
                else
                {
                    int color = ResourcesCompat.getColor(getResources(), R.color.btn_disabled_color, null);
                    Drawable drawable = binding.btnNext.getBackground();
                    drawable.setTint(color);
                    binding.btnNext.setBackground(drawable);
                    binding.btnNext.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.btnNext.setOnClickListener((view) -> {
            if (!binding.etPassword.getText().toString().equals(binding.etRePassword.getText().toString())) {
                ToastUtils.showToastError(SignUpActivity.this, "The passwords entered do not match", Toast.LENGTH_SHORT);
                return;
            }

            startLoadingIndicator();
            String name = String.valueOf(binding.name.getText());
            if(name == null || name.isEmpty())
                name = "Anonymous";
            String email = String.valueOf(binding.etEmail.getText());
            String password = String.valueOf(binding.etPassword.getText());
            UserModel newUser = new UserModel(name, email, password);
            viewModel.signUp(newUser, new SignUpViewModel.SignUpCallback() {
                @Override
                public void onSuccess() {
                    ActivityUtils.switchToActivity(SignUpActivity.this, LogInActivity.class);
                    finish();
                }

                @Override
                public void onFailure(String message) {
                    ToastUtils.showToastError(SignUpActivity.this, message, Toast.LENGTH_SHORT);
                    stopLoadingIndicator();
                }
            });
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpActivity.super.onBackPressed();
                finish();
            }
        });

        setContentView(binding.getRoot());
    }

    private void startLoadingIndicator() {
        binding.loadingIndicator.setVisibility(View.VISIBLE);
        binding.mainLayout.setClickable(false);
        binding.mainLayout.setAlpha(0.8f); // Adjust the opacity to your preference
    }

    private void stopLoadingIndicator() {
        binding.loadingIndicator.setVisibility(View.GONE);
        binding.mainLayout.setClickable(true);
        binding.mainLayout.setAlpha(1f);
    }
}