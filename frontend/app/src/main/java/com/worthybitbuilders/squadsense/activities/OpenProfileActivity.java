package com.worthybitbuilders.squadsense.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.databinding.ActivityOpenProfileBinding;
import com.worthybitbuilders.squadsense.models.UserModel;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.utils.ActivityUtils;
import com.worthybitbuilders.squadsense.viewmodels.UserViewModel;

public class OpenProfileActivity extends AppCompatActivity {
    private ActivityOpenProfileBinding binding;
    private UserViewModel userViewModel;

    private Uri avatarUri;

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
        userViewModel.getUserById(SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USERID), new UserViewModel.UserCallback() {
            @Override
            public void onSuccess(UserModel user) {
                String name = user.getName();
                String email = user.getEmail();
                String introduction = user.getIntroduction();
                String phonenumber = user.getPhoneNumber();
                String birthday = user.getBirthday();
                String avatarUriString = user.getProfileImagePath();
                int color;

                //set name
                if(name == null || name.isEmpty())
                    name = "Anonymous";
                binding.name.setText(name);
                binding.defaultImageProfile.setText(String.valueOf(name.charAt(0)));

//                //set avatar
//                if(avatarUriString != null && !avatarUriString.isEmpty())
//                {
//                    avatarUri = Uri.parse(avatarUriString);
//                    binding.imageProfile.setImageURI(avatarUri);
//                    binding.defaultImageProfile.setVisibility(View.GONE);
//                    binding.imageProfileBorder.setVisibility(View.VISIBLE);
//                }

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
            }

            @Override
            public void onFailure(String message) {
                Toast t = Toast.makeText(OpenProfileActivity.this, message, Toast.LENGTH_SHORT);
                t.setGravity(Gravity.TOP, 0, 0);
                t.show();
            }
        });
    }
}