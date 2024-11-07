package com.worthybitbuilders.squadsense.activities;

import android.app.Dialog;
import android.os.Bundle;
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
import com.worthybitbuilders.squadsense.utils.ToastUtils;
import com.worthybitbuilders.squadsense.viewmodels.UserViewModel;

public class OpenProfileActivity extends AppCompatActivity {
    private ActivityOpenProfileBinding binding;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);;
        getSupportActionBar().hide();
        binding = ActivityOpenProfileBinding.inflate(getLayoutInflater());
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

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
                showEditActivity();
            }
        });

        binding.btnIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditActivity();
            }
        });

        binding.btnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditActivity();
            }
        });
        binding.btnBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditActivity();
            }
        });
        binding.defaultImageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditActivity();
            }
        });
        binding.imageProfileBorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditActivity();
            }
        });

        setContentView(binding.getRoot());
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        LoadData();
    }

    private void showEditActivity() {
        ActivityUtils.switchToActivity(getWindow().getContext(), EditProfileActivity.class);
    }

    private void LoadData()
    {
        Dialog loadingDialog = DialogUtils.GetLoadingDialog(this);
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
                        Glide.with(OpenProfileActivity.this)
                                .load(user.getProfileImagePath())
                                .into(binding.imageProfile);
                        loadAvatarView(true);
                    }
                    catch (Exception e)
                    {
                        ToastUtils.showToastError(OpenProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
                    }
                }
                else
                    loadAvatarView(false);

                //set introduction
                color = getResources().getColor(R.color.primary_word_color, getTheme());
                if(introduction == null || introduction.isEmpty())
                {
                    introduction = "Add introduction";
                    color = getResources().getColor(R.color.primary_word_second_color, getTheme());
                }
                binding.introduction.setText(introduction);
                binding.introduction.setTextColor(color);

                //set email
                binding.email.setText(email);

                //set phonenumber
                color = getResources().getColor(R.color.primary_word_color, getTheme());
                if(phonenumber == null || phonenumber.isEmpty())
                {
                    phonenumber = "Add a phone number";
                    color = getResources().getColor(R.color.primary_word_second_color, getTheme());
                }
                binding.phonenumber.setText(phonenumber);
                binding.phonenumber.setTextColor(color);

                //set birthday
                color = getResources().getColor(R.color.primary_word_color, getTheme());
                if(birthday == null || birthday.isEmpty())
                {
                    birthday = "Add a birthday";
                    color = getResources().getColor(R.color.primary_word_second_color, getTheme());
                }
                binding.birthday.setText(birthday);
                binding.birthday.setTextColor(color);

                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(String message) {
                loadingDialog.dismiss();
                ToastUtils.showToastError(OpenProfileActivity.this, message, Toast.LENGTH_SHORT);
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