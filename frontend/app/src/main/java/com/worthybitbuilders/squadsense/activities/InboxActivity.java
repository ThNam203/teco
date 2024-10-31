package com.worthybitbuilders.squadsense.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.adapters.FriendItemAdapter;
import com.worthybitbuilders.squadsense.databinding.ActivityInboxBinding;
import com.worthybitbuilders.squadsense.databinding.AddNewChatRoomPopupBinding;
import com.worthybitbuilders.squadsense.models.ChatRoom;
import com.worthybitbuilders.squadsense.models.UserModel;
import com.worthybitbuilders.squadsense.utils.DialogUtils;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.viewmodels.ChatRoomViewModel;
import com.worthybitbuilders.squadsense.viewmodels.FriendViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InboxActivity extends AppCompatActivity {
    ActivityInboxBinding binding;
    FriendViewModel friendViewModel;
    ChatRoomViewModel chatRoomViewModel;
    Dialog loadingDialog;
    private FriendItemAdapter friendItemAdapter;
    private final List<UserModel> friendList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        binding = ActivityInboxBinding.inflate(getLayoutInflater());
        friendViewModel = new ViewModelProvider(this).get(FriendViewModel.class);
        chatRoomViewModel = new ViewModelProvider(this).get(ChatRoomViewModel.class);

        friendItemAdapter = new FriendItemAdapter(this, friendList);
        loadingDialog = DialogUtils.GetLoadingDialog(this);

        loadChatRooms();

        binding.btnAddChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddChatRoomPopup();
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

    private void showAddChatRoomPopup() {
        AddNewChatRoomPopupBinding popupBinding = AddNewChatRoomPopupBinding.inflate(getLayoutInflater());
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(popupBinding.getRoot());

        LoadFriends(popupBinding);
        popupBinding.btnBack.setOnClickListener(view -> dialog.dismiss());

        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.show();
    }

    private void loadChatRooms()
    {
        chatRoomViewModel.getChatRooms(new ChatRoomViewModel.ApiCallHandler() {
            @Override
            public void onSuccess() {
                loadingDialog.dismiss();
                List<ChatRoom> chatRooms = chatRoomViewModel.getChatRoomsLiveData().getValue();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(InboxActivity.this, message, Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            }
        });
    }

    private void LoadFriends(AddNewChatRoomPopupBinding popupBinding)
    {
        loadingDialog.show();
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        friendViewModel.getFriendById(userId, new FriendViewModel.getFriendCallback() {
            @Override
            public void onSuccess(List<UserModel> friends) {
                friendList.clear();
                friendList.addAll(friends);
                popupBinding.rvFriends.setLayoutManager(new LinearLayoutManager(InboxActivity.this));
                popupBinding.rvFriends.setAdapter(friendItemAdapter);
                updateFriendUI(popupBinding);
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(InboxActivity.this, message, Toast.LENGTH_SHORT).show();
                updateFriendUI(popupBinding);
                loadingDialog.dismiss();
            }
        });

        friendItemAdapter.setOnClickListener(position -> {

        });
    }


    private void updateFriendUI(AddNewChatRoomPopupBinding popupBinding)
    {
        if(friendList.size() > 0)
        {
            popupBinding.imageNoFriendFound.setVisibility(View.GONE);
            popupBinding.rvFriends.setVisibility(View.VISIBLE);

        }
        else
        {
            popupBinding.imageNoFriendFound.setVisibility(View.VISIBLE);
            popupBinding.rvFriends.setVisibility(View.GONE);
        }
    }
}