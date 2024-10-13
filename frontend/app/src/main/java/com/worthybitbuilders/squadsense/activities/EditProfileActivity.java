package com.worthybitbuilders.squadsense.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.databinding.ActivityEditProfileBinding;
import com.worthybitbuilders.squadsense.databinding.ActivityOpenProfileBinding;
import com.worthybitbuilders.squadsense.models.UserModel;
import com.worthybitbuilders.squadsense.utils.Activity;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.viewmodels.UserViewModel;

public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding binding;

    private UserViewModel userViewModel;

    private UserModel currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        userViewModel.getUserById(SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USERID), new UserViewModel.UserCallback() {
            @Override
            public void onSuccess(UserModel user) {
                currentUser = user;
                if(user.getName() != null && !user.getName().isEmpty())
                {
                    binding.name.setText(user.getName());
                    binding.defaultImageProfile.setText(String.valueOf(binding.name.getText().charAt(0)));
                }
                else {
                    binding.name.setText("Anonymous");
                    binding.defaultImageProfile.setText("A");
                }

                if(user.getIntroduction() != null && !user.getIntroduction().isEmpty())
                {
                    binding.introduction.setText(user.getIntroduction());
                }

                if(user.getEmail() != null && !user.getEmail().isEmpty())
                {
                    binding.email.setText(user.getEmail());
                }

                if(user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty())
                {
                    binding.phonenumber.setText(user.getPhoneNumber());
                }

                if(user.getBirthday() != null && !user.getBirthday().isEmpty())
                {
                    binding.birthday.setText(user.getBirthday());
                }
            }

            @Override
            public void onFailure(String message) {
                Toast t = Toast.makeText(EditProfileActivity.this, message, Toast.LENGTH_SHORT);
                t.setGravity(Gravity.TOP, 0, 0);
                t.show();
            }
        });

        //set onclick buttons here
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditProfileActivity.super.onBackPressed();
            }
        });

        binding.btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnCamera_showPopup();
            }
        });

        binding.btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.name.getText().toString() != null && !binding.name.getText().toString().isEmpty())
                    currentUser.setName(binding.name.getText().toString());
                else
                    currentUser.setName("Anonymous");
                currentUser.setIntroduction(binding.introduction.getText().toString());
                currentUser.setPhoneNumber(binding.phonenumber.getText().toString());
                currentUser.setBirthday(binding.birthday.getText().toString());
                userViewModel.updateUser(currentUser, new UserViewModel.UserCallback() {
                    @Override
                    public void onSuccess(UserModel user) {
                        Toast t = Toast.makeText(EditProfileActivity.this, "user updated!", Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.TOP, 0, 0);
                        t.show();

                        EditProfileActivity.super.onBackPressed();
                        finish();
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast t = Toast.makeText(EditProfileActivity.this, message, Toast.LENGTH_SHORT);
                        t.setGravity(Gravity.TOP, 0, 0);
                        t.show();
                    }
                });
            }
        });
        setContentView(binding.getRoot());
    }



    //define function here
    private void btnCamera_showPopup() {
        final Dialog dialog = new Dialog(getWindow().getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_of_camera);
        //Set activity of button in dialog here


        //
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
    }
}