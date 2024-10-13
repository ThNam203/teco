package com.worthybitbuilders.squadsense.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.worthybitbuilders.squadsense.MainActivity;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.databinding.PageLoginBinding;
import com.worthybitbuilders.squadsense.utils.SwitchActivity;
import com.worthybitbuilders.squadsense.viewmodels.LoginViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LogInActivity extends AppCompatActivity {
    private PageLoginBinding binding;
    LoginViewModel loginViewModel;
    GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = PageLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        setUpGoogleLogin();

        binding.loginEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence content, int i, int i1, int i2) {
                String inputEmail = content.toString();
                if(loginViewModel.IsValidEmail(inputEmail))
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

        binding.btnGoToSignUp.setOnClickListener((view) -> {
            SwitchActivity.switchToActivity(this, SignUpActivity.class);
            finish();
        });

        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputEmail = binding.loginEmail.getText().toString();
                String inputPassword = binding.loginPassword.getText().toString();
                if(!loginViewModel.IsValidEmail(inputEmail))
                {
                    Toast.makeText(LogInActivity.this, "Invalid email", Toast.LENGTH_SHORT).show();
                    return;
                }

                startLoadingIndicator();
                loginViewModel.logIn(inputEmail, inputPassword, new LoginViewModel.LogInCallback() {
                    @Override
                    public void onSuccess() {
                        stopLoadingIndicator();
                        SwitchActivity.switchToActivity(LogInActivity.this, MainActivity.class);
                        finish();
                    }

                    @Override
                    public void onFailure(String message) {
                        stopLoadingIndicator();
                        Toast.makeText(LogInActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
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

    private void setUpGoogleLogin() {
        Toast.makeText(this, "Clicked", Toast.LENGTH_LONG).show();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.btnLoginGoogle.setOnClickListener(view -> {
            Toast.makeText(this, "Clicked", Toast.LENGTH_LONG).show();
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, 1);
        });
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Toast.makeText(this, account.getEmail(), Toast.LENGTH_LONG).show();
        } catch (ApiException e) {
            Toast.makeText(this, "Login with Google failed", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle the result of the activity here
        if (requestCode == 1) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
}