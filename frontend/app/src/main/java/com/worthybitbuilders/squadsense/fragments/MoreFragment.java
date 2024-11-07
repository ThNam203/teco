package com.worthybitbuilders.squadsense.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.activities.FriendActivity;
import com.worthybitbuilders.squadsense.activities.InboxActivity;
import com.worthybitbuilders.squadsense.activities.LogInActivity;
import com.worthybitbuilders.squadsense.activities.NotificationSettingActivity;
import com.worthybitbuilders.squadsense.activities.OpenProfileActivity;
import com.worthybitbuilders.squadsense.databinding.FragmentMoreBinding;
import com.worthybitbuilders.squadsense.models.UserModel;
import com.worthybitbuilders.squadsense.utils.ActivityUtils;
import com.worthybitbuilders.squadsense.utils.DialogUtils;
import com.worthybitbuilders.squadsense.utils.EventChecker;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.utils.SocketClient;
import com.worthybitbuilders.squadsense.utils.ToastUtils;
import com.worthybitbuilders.squadsense.viewmodels.FriendViewModel;
import com.worthybitbuilders.squadsense.viewmodels.UserViewModel;

import java.util.List;

public class MoreFragment extends Fragment {

    private FragmentMoreBinding binding;
    private UserViewModel userViewModel;
    private FriendViewModel friendViewModel;
    Dialog loadingDialog;

    EventChecker eventChecker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMoreBinding.inflate(getLayoutInflater());
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        friendViewModel = new ViewModelProvider(this).get(FriendViewModel.class);
        eventChecker = new EventChecker();
        loadingDialog = DialogUtils.GetLoadingDialog(getContext());

        LoadData();

        //set onclick of buttons here
        binding.btnNotificationSettings.setOnClickListener(view -> btnNotificationSetting_showActivity());
        binding.btnTheme.setOnClickListener(view -> btnTheme_showPopup());
        binding.btnInbox.setOnClickListener(view -> btnInbox_showActivity());
        binding.profile.setOnClickListener(view -> btnProfile_showActivity());
        binding.btnFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnFriend_showActivity();
            }
        });
        binding.btnLogout.setOnClickListener(view -> {
            ActivityUtils.switchToActivity(getContext(), LogInActivity.class);
            SharedPreferencesManager.clearData();
            SocketClient.getInstance().disconnect();
            getActivity().finish();
        });
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        LoadData();
    }

    private void btnProfile_showActivity() {
        ActivityUtils.switchToActivity(getContext(), OpenProfileActivity.class);
    }

    private void btnInbox_showActivity() {
        ActivityUtils.switchToActivity(getContext(), InboxActivity.class);
    }
    private void btnFriend_showActivity() {
        ActivityUtils.switchToActivity(getContext(), FriendActivity.class);
    }

    private void btnNotificationSetting_showActivity() {
        ActivityUtils.switchToActivity(getContext(), NotificationSettingActivity.class);
    }

    private void btnTheme_showPopup() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_theme);

        SwitchCompat toggleSwitchTheme = (SwitchCompat) dialog.findViewById(R.id.toggle_switch_theme);
        TextView tvThemeName = (TextView)  dialog.findViewById(R.id.theme_name);
        toggleSwitchTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked)
                {
                    tvThemeName.setText("Light theme");

                }
                else
                {
                    tvThemeName.setText("Dark theme");
                }
            }
        });

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
    }

    private void LoadData(){
        loadingDialog.show();
        eventChecker.setActionWhenComplete(new EventChecker.CompleteCallback() {
            @Override
            public void Action() {
                loadingDialog.dismiss();
            }
        });
        LoadAvatarData();
        LoadBtnYourFriend();
    }

    private void LoadAvatarData()
    {
        int LOAD_AVATAR_DATA_CODE = eventChecker.addEventStatusAndGetCode();
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
                        Glide.with(getContext())
                                .load(user.getProfileImagePath())
                                .into(binding.imageProfile);
                        loadAvatarView(true);
                    }
                    catch (Exception e)
                    {
                        ToastUtils.showToastError(getContext(), e.getMessage(), Toast.LENGTH_SHORT);
                    }
                }
                else
                    loadAvatarView(false);

                eventChecker.markEventAsCompleteAndDoActionIfNeeded(LOAD_AVATAR_DATA_CODE);
            }

            @Override
            public void onFailure(String message) {
                eventChecker.markEventAsCompleteAndDoActionIfNeeded(LOAD_AVATAR_DATA_CODE);
                ToastUtils.showToastError(getContext(), message, Toast.LENGTH_SHORT);
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

    private void LoadBtnYourFriend()
    {
        int LOAD_BTN_YOUR_FRIEND = eventChecker.addEventStatusAndGetCode();
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        friendViewModel.getFriendById(userId, new FriendViewModel.getFriendCallback() {
            @Override
            public void onSuccess(List<UserModel> friends) {
                int size = friends.size();
                String descFriend = String.valueOf(size) + " " + (size < 2 ? "person" : "persons");
                binding.descFriend.setText(descFriend);
                eventChecker.markEventAsCompleteAndDoActionIfNeeded(LOAD_BTN_YOUR_FRIEND);
            }

            @Override
            public void onFailure(String message) {
                ToastUtils.showToastError(getContext(), message, Toast.LENGTH_SHORT);
                eventChecker.markEventAsCompleteAndDoActionIfNeeded(LOAD_BTN_YOUR_FRIEND);
            }
        });
    }
}