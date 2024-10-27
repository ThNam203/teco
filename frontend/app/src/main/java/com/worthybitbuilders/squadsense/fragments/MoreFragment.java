package com.worthybitbuilders.squadsense.fragments;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.worthybitbuilders.squadsense.activities.EditProfileActivity;
import com.worthybitbuilders.squadsense.activities.LogInActivity;
import com.worthybitbuilders.squadsense.activities.InboxActivity;
import com.worthybitbuilders.squadsense.activities.TeamActivity;
import com.worthybitbuilders.squadsense.activities.NotificationSettingActivity;
import com.worthybitbuilders.squadsense.activities.OpenProfileActivity;
import com.worthybitbuilders.squadsense.activities.SearchEverywhereActivity;
import com.worthybitbuilders.squadsense.databinding.FragmentMoreBinding;
import com.worthybitbuilders.squadsense.models.UserModel;
import com.worthybitbuilders.squadsense.utils.DialogUtils;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.utils.ActivityUtils;
import com.worthybitbuilders.squadsense.viewmodels.UserViewModel;

public class MoreFragment extends Fragment {

    private FragmentMoreBinding binding;
    private UserViewModel userViewModel;
    Dialog loadingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMoreBinding.inflate(getLayoutInflater());

        loadingDialog = DialogUtils.GetLoadingDialog(getContext());

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        LoadData();

        //set onclick of buttons here
        binding.btnNotificationSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnNotificationSetting_showActivity();
            }
        });

        binding.btnInbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnInbox_showActivity();
            }
        });

        binding.btnMyteam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnMyteam_showActivity();
            }
        });

        binding.btnSearchEverywhere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSearchEverywhere_showActivity();
            }
        });
        binding.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnProfile_showActivity();
            }
        });
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityUtils.switchToActivity(getContext(), LogInActivity.class);
                getActivity().finish();
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        LoadData();
    }

    private void btnSearchEverywhere_showActivity() {
        ActivityUtils.switchToActivity(getContext(), SearchEverywhereActivity.class);
    }

    private void btnProfile_showActivity() {
        ActivityUtils.switchToActivity(getContext(), OpenProfileActivity.class);
    }

    private void btnMyteam_showActivity() {
        ActivityUtils.switchToActivity(getContext(), TeamActivity.class);
    }

    private void btnInbox_showActivity() {
        ActivityUtils.switchToActivity(getContext(), InboxActivity.class);
    }

    private void btnNotificationSetting_showActivity() {
        ActivityUtils.switchToActivity(getContext(), NotificationSettingActivity.class);
    }

    private void LoadData(){
        loadingDialog.show();
        userViewModel.getUserById(SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID), new UserViewModel.UserCallback() {
            @Override
            public void onSuccess(UserModel user) {
                if(user.getName() == null || user.getName().isEmpty())
                {
                    binding.name.setText("Anonymous");
                    binding.defaultImageProfile.setText("A");
                }
                else
                {
                    binding.name.setText(user.getName());
                    binding.defaultImageProfile.setText(String.valueOf(binding.name.getText().charAt(0)));
                }

                if(user.getProfileImagePath() != null && !user.getProfileImagePath().isEmpty())
                {
                    try{
                        String profileImagePath = user.getProfileImagePath();
                        String publicProfileImageURL = String.format("https://squadsense.s3.ap-southeast-1.amazonaws.com/%s", profileImagePath);

                        Glide.with(getContext())
                                .load(publicProfileImageURL)
                                .into(binding.imageProfile);
                        loadAvatarView(true);
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else
                    loadAvatarView(false);

                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(String message) {
                loadingDialog.dismiss();
                Toast t = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
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