package com.worthybitbuilders.squadsense.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.databinding.ActivityInboxBinding;
import com.worthybitbuilders.squadsense.models.UserModel;
import com.worthybitbuilders.squadsense.utils.DialogUtils;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.viewmodels.FriendViewModel;
import com.worthybitbuilders.squadsense.viewmodels.LoginViewModel;

import java.util.ArrayList;
import java.util.List;

public class InboxActivity extends AppCompatActivity {
    ActivityInboxBinding binding;

    FriendViewModel friendViewModel;

    Dialog loadingDialog;
    private List<UserModel> listFriends = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityInboxBinding.inflate(getLayoutInflater());
        friendViewModel = new ViewModelProvider(this).get(FriendViewModel.class);

        loadingDialog = DialogUtils.GetLoadingDialog(this);
        loadingDialog.show();
        LoadFriends();

        binding.btnAddChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAddChatAction();
            }
        });

        //set onclick buttons here
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InboxActivity.super.onBackPressed();
            }
        });
        setContentView(binding.getRoot());
    }

    private void LoadFriends()
    {
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        friendViewModel.getFriendById(userId, new FriendViewModel.getFriendCallback() {
            @Override
            public void onSuccess(List<UserModel> friends) {
                for (UserModel friend : friends)
                {
                    listFriends.add(friend);
                }

                LoadInboxView();
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(InboxActivity.this, message, Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            }
        });
    }

    private void LoadInboxView()
    {
        if(listFriends.size() > 0)
        {
            binding.defaultInbox.setVisibility(View.GONE);
            binding.rvInbox.setVisibility(View.VISIBLE);
        }
        else
        {
            binding.defaultInbox.setVisibility(View.VISIBLE);
            binding.rvInbox.setVisibility(View.GONE);
        }
    }

    private void btnAddChatAction()
    {

    }
}