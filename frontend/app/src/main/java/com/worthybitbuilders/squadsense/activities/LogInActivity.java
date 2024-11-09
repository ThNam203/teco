package com.worthybitbuilders.squadsense.activities;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.worthybitbuilders.squadsense.MainActivity;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.databinding.PageLoginBinding;
import com.worthybitbuilders.squadsense.utils.ActivityUtils;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.utils.SocketClient;
import com.worthybitbuilders.squadsense.utils.ToastUtils;
import com.worthybitbuilders.squadsense.viewmodels.LoginViewModel;

import java.util.Objects;

import pub.devrel.easypermissions.EasyPermissions;

public class LogInActivity extends AppCompatActivity {
    private PageLoginBinding binding;
    LoginViewModel loginViewModel;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = PageLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).hide();

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Auto logging
        if (loginViewModel.isAutoLogging()) ActivityUtils.switchToActivity(LogInActivity.this, MainActivity.class);

        setUpGoogleLogin();

        SharedPreferencesManager.init(this);

        binding.loginEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence content, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.btnGotoSignup.setOnClickListener((view) -> {
            ActivityUtils.switchToActivity(this, SignUpActivity.class);
        });

        binding.btnLogin.setOnClickListener(view -> {
            String inputEmail = binding.loginEmail.getText().toString();
            String inputPassword = binding.loginPassword.getText().toString();
            if(inputPassword.isEmpty())
            {
                ToastUtils.showToastError(LogInActivity.this, "Missing password!", Toast.LENGTH_SHORT);
                return;
            }

            if(!loginViewModel.isValidEmail(inputEmail))
            {
                ToastUtils.showToastError(LogInActivity.this, "Invalid email", Toast.LENGTH_SHORT);
                return;
            }

                startLoadingIndicator();
                loginViewModel.logIn(inputEmail, inputPassword, new LoginViewModel.LogInCallback() {
                    @Override
                    public void onSuccess(String userId) {
                        stopLoadingIndicator();
                        SocketClient.InitializeIO(getApplication(), userId);
                        SharedPreferencesManager.saveData(SharedPreferencesManager.KEYS.USER_EMAIL, inputEmail);
                        ActivityUtils.switchToActivity(LogInActivity.this, MainActivity.class);
                        LogInActivity.this.finish();
                    }

                @Override
                public void onFailure(String message) {
                    stopLoadingIndicator();
                    ToastUtils.showToastError(LogInActivity.this, message, Toast.LENGTH_LONG);
                }
            });
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            String[] permissions = {Manifest.permission.POST_NOTIFICATIONS};
            EasyPermissions.requestPermissions(this, "", 0, permissions);
        }
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
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.btnLoginGoogle.setOnClickListener(view -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityIfNeeded(signInIntent, 1);
        });
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
//        try {
            // TODO: GOOGLE SIGN IN
//            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//        } catch (ApiException e) {
//            ToastUtils.showToastError(this, "Login with Google failed", Toast.LENGTH_LONG);
//        }
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