package com.worthybitbuilders.squadsense.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.databinding.ActivityMemberBinding;
import com.worthybitbuilders.squadsense.databinding.ActivityMemberInfoBinding;
import com.worthybitbuilders.squadsense.models.UserModel;
import com.worthybitbuilders.squadsense.utils.DialogUtils;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.utils.ToastUtils;
import com.worthybitbuilders.squadsense.viewmodels.UserViewModel;

public class MemberInfoActivity extends AppCompatActivity {

    ActivityMemberInfoBinding binding;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityMemberInfoBinding.inflate(getLayoutInflater());
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        LoadData();

        //set onclick buttons here
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MemberInfoActivity.super.onBackPressed();
            }
        });

        setContentView(binding.getRoot());
    }

    private void LoadData()
    {
        String memberId = getIntent().getStringExtra("memberId");
        Dialog loadingDialog = DialogUtils.GetLoadingDialog(this);
        loadingDialog.show();
        userViewModel.getUserById(memberId, new UserViewModel.UserCallback() {
            @Override
            public void onSuccess(UserModel user) {
                String name = user.getName();
                String email = user.getEmail();
                String introduction = user.getIntroduction();
                String phonenumber = user.getPhoneNumber();
                String birthday = user.getBirthday();

                //set name
                if(name == null || name.isEmpty())
                    name = "Anonymous";
                binding.name.setText(name);
                binding.defaultImageProfile.setText(String.valueOf(name.charAt(0)));

                if(user.getProfileImagePath() != null && !user.getProfileImagePath().isEmpty())
                {
                    try{
                        Glide.with(MemberInfoActivity.this)
                                .load(user.getProfileImagePath())
                                .into(binding.imageProfile);
                        loadAvatarView(true);
                    }
                    catch (Exception e)
                    {
                        ToastUtils.showToastError(MemberInfoActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
                    }
                }
                else
                    loadAvatarView(false);

                //set introduction
                if(introduction != null && !introduction.isEmpty())
                    binding.introduction.setText(introduction);

                //set email
                binding.email.setText(email);

                //set phonenumber
                if(phonenumber != null && !phonenumber.isEmpty())
                    binding.phonenumber.setText(phonenumber);

                //set birthday
                if(birthday != null && !birthday.isEmpty())
                    binding.birthday.setText(birthday);


                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(String message) {
                loadingDialog.dismiss();
                ToastUtils.showToastError(MemberInfoActivity.this, message, Toast.LENGTH_SHORT);
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