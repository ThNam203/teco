package com.worthybitbuilders.squadsense.Pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.worthybitbuilders.squadsense.MainActivity;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.Util.SwitchActivity;
import com.worthybitbuilders.squadsense.ViewModels.LoginViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class page_login extends AppCompatActivity {
    EditText loginEmail;
    EditText loginPassword;
    AppCompatButton btnNext;
    Button btnLoginWithGoogle;
    LoginViewModel loginViewModel;
    GoogleSignInClient mGoogleSignInClient;
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

        setUpGoogleLogin();

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

    private void setUpGoogleLogin() {
        Toast.makeText(this, "Clicked", Toast.LENGTH_LONG).show();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnLoginWithGoogle = (Button) findViewById(R.id.btn_login_google);
        btnLoginWithGoogle.setOnClickListener(view -> {
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