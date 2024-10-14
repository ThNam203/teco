package com.worthybitbuilders.squadsense.fragments12;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.worthybitbuilders.squadsense.activities.LogInActivity;
import com.worthybitbuilders.squadsense.activities.InboxActivity;
import com.worthybitbuilders.squadsense.activities.TeamActivity;
import com.worthybitbuilders.squadsense.activities.NotificationSettingActivity;
import com.worthybitbuilders.squadsense.activities.OpenProfileActivity;
import com.worthybitbuilders.squadsense.activities.SearchEverywhereActivity;
import com.worthybitbuilders.squadsense.models.UserModel;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.utils.Activity;
import com.worthybitbuilders.squadsense.viewmodels.UserViewModel;

public class MoreFragment extends Fragment {

    private FragmentMoreBinding binding;

    private UserViewModel userViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMoreBinding.inflate(getLayoutInflater());

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
                Activity.switchToActivity(getContext(), LogInActivity.class);
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
        Activity.switchToActivity(getContext(), SearchEverywhereActivity.class);
    }

    private void btnProfile_showActivity() {
        Activity.switchToActivity(getContext(), OpenProfileActivity.class);
    }

    private void btnMyteam_showActivity() {
        Activity.switchToActivity(getContext(), TeamActivity.class);
    }

    private void btnInbox_showActivity() {
        Activity.switchToActivity(getContext(), InboxActivity.class);
    }

    private void btnNotificationSetting_showActivity() {
        Activity.switchToActivity(getContext(), NotificationSettingActivity.class);
    }

    private void LoadData(){
        userViewModel.getUserById(SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USERID), new UserViewModel.UserCallback() {
            @Override
            public void onSuccess(UserModel user) {
                if(user.getName() == null || user.getName().equals(""))
                {
                    binding.name.setVisibility(View.GONE);
                    binding.imageProfile.setText("N");
                }
                else
                {
                    binding.name.setText(user.getName());
                    binding.name.setVisibility(View.VISIBLE);
                    binding.imageProfile.setText(String.valueOf(binding.name.getText().charAt(0)));
                }
            }

            @Override
            public void onFailure(String message) {
                Toast t = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
                t.setGravity(Gravity.TOP, 0, 0);
                t.show();
            }
        });
    }
}