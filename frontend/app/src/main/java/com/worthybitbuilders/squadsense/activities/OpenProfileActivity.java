package com.worthybitbuilders.squadsense.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.databinding.ActivityOpenProfileBinding;
import com.worthybitbuilders.squadsense.models.UserModel;
import com.worthybitbuilders.squadsense.utils.ActivityUtils;
import com.worthybitbuilders.squadsense.utils.DialogUtils;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.viewmodels.UserViewModel;

public class OpenProfileActivity extends AppCompatActivity {
    private ActivityOpenProfileBinding binding;
    private UserViewModel userViewModel;
    Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);;
        getSupportActionBar().hide();
        binding = ActivityOpenProfileBinding.inflate(getLayoutInflater());
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        loadingDialog = DialogUtils.GetLoadingDialog(this);
        LoadData();

        //set onclick buttons here
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenProfileActivity.super.onBackPressed();
            }
        });
        binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnEdit_showActivity();
            }
        });

        setContentView(binding.getRoot());
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        LoadData();
    }

    private void btnEdit_showActivity() {
        ActivityUtils.switchToActivity(getWindow().getContext(), EditProfileActivity.class);
    }

    private void LoadData()
    {
        loadingDialog.show();
        userViewModel.getUserById(SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID), new UserViewModel.UserCallback() {
            @Override
            public void onSuccess(UserModel user) {
                String name = user.getName();
                String email = user.getEmail();
                String introduction = user.getIntroduction();
                String phonenumber = user.getPhoneNumber();
                String birthday = user.getBirthday();
                int color;

                //set name
                if(name == null || name.isEmpty())
                    name = "Anonymous";
                binding.name.setText(name);
                binding.defaultImageProfile.setText(String.valueOf(name.charAt(0)));

                if(user.getProfileImagePath() != null && !user.getProfileImagePath().isEmpty())
                {
                    try{
                        String profileImagePath = user.getProfileImagePath();
                        String publicProfileImageURL = String.format("https://squadsense.s3.ap-southeast-1.amazonaws.com/%s", profileImagePath);

                        Glide.with(OpenProfileActivity.this)
                                .load(publicProfileImageURL)
                                .into(binding.imageProfile);
                        loadAvatarView(true);
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(OpenProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else
                    loadAvatarView(false);

                //set introduction
                color = getResources().getColor(R.color.primary_word_color);
                if(introduction == null || introduction.isEmpty())
                {
                    introduction = "Add introduction";
                    color = getResources().getColor(R.color.primary_word_second_color);
                }
                binding.introduction.setText(introduction);
                binding.introduction.setTextColor(color);

                //set email
                binding.email.setText(email);

                //set phonenumber
                color = getResources().getColor(R.color.primary_word_color);
                if(phonenumber == null || phonenumber.isEmpty())
                {
                    phonenumber = "Add a phone number";
                    color = getResources().getColor(R.color.primary_word_second_color);
                }
                binding.phonenumber.setText(phonenumber);
                binding.phonenumber.setTextColor(color);

                //set birthday
                color = getResources().getColor(R.color.primary_word_color);
                if(birthday == null || birthday.isEmpty())
                {
                    birthday = "Add a birthday";
                    color = getResources().getColor(R.color.primary_word_second_color);
                }
                binding.birthday.setText(birthday);
                binding.birthday.setTextColor(color);

                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(String message) {
                loadingDialog.dismiss();
                Toast t = Toast.makeText(OpenProfileActivity.this, message, Toast.LENGTH_SHORT);
                t.setGravity(Gravity.TOP, 0, 0);
                t.show();
            }
        });
    }
    private void loadAvatarView(boolean hasAvatar)
    {
        if(hasAvatar)
        {
            binding.imageProfileBorder.setVisibility(View.VISIBLE);
            binding.defaultImageProfile.setVisibility(View.GONE);
        }
        else
        {
            binding.imageProfileBorder.setVisibility(View.GONE);
            binding.defaultImageProfile.setVisibility(View.VISIBLE);
        }
    }
}