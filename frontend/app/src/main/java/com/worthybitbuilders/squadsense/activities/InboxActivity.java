package com.worthybitbuilders.squadsense.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.adapters.ChatRoomAdapter;
import com.worthybitbuilders.squadsense.adapters.FriendItemAdapter;
import com.worthybitbuilders.squadsense.databinding.ActivityInboxBinding;
import com.worthybitbuilders.squadsense.databinding.AddNewChatRoomPopupBinding;
import com.worthybitbuilders.squadsense.models.ChatRoom;
import com.worthybitbuilders.squadsense.models.UserModel;
import com.worthybitbuilders.squadsense.utils.DialogUtils;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.viewmodels.ChatRoomViewModel;
import com.worthybitbuilders.squadsense.viewmodels.FriendViewModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class InboxActivity extends AppCompatActivity {
    ActivityInboxBinding binding;
    FriendViewModel friendViewModel;
    ChatRoomViewModel chatRoomViewModel;
    Dialog loadingDialog;
    private FriendItemAdapter friendItemAdapter;
    private ChatRoomAdapter chatRoomAdapter;
    private final List<UserModel> friendList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        binding = ActivityInboxBinding.inflate(getLayoutInflater());
        loadingDialog = DialogUtils.GetLoadingDialog(this);
        setContentView(binding.getRoot());

        friendViewModel = new ViewModelProvider(this).get(FriendViewModel.class);
        chatRoomViewModel = new ViewModelProvider(this).get(ChatRoomViewModel.class);

        binding.rvInbox.setLayoutManager(new LinearLayoutManager(this));
        friendItemAdapter = new FriendItemAdapter(friendList);
        loadChatRooms();

        binding.btnAddChat.setOnClickListener(view -> showAddChatRoomPopup());
        binding.btnBack.setOnClickListener(view -> InboxActivity.super.onBackPressed());
    }

    private void showAddChatRoomPopup() {
        AddNewChatRoomPopupBinding popupBinding = AddNewChatRoomPopupBinding.inflate(getLayoutInflater());
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(popupBinding.getRoot());

        LoadFriends(popupBinding, dialog);
        popupBinding.btnBack.setOnClickListener(view -> dialog.dismiss());

        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.show();
    }

    private void loadChatRooms()
    {
        chatRoomViewModel.getChatRoomsRemotely(new ChatRoomViewModel.ApiCallHandler() {
            @Override
            public void onSuccess() {
                loadingDialog.dismiss();
                chatRoomAdapter = new ChatRoomAdapter(chatRoomViewModel.getChatRooms(), chatRoom -> changeToMessagingActivity(chatRoom));
                updateChatRoomUI();
                binding.rvInbox.setAdapter(chatRoomAdapter);
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(InboxActivity.this, message, Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            }
        });
    }

    private void LoadFriends(AddNewChatRoomPopupBinding popupBinding, Dialog popupDialog)
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
            popupDialog.dismiss();

            List<String> memberIds = new ArrayList<>();
            memberIds.add(friendList.get(position).getId());
            memberIds.add(userId);
            Collections.sort(memberIds);

            // check if there is already a chat room with 2 user
            List<ChatRoom> availChatRooms = chatRoomViewModel.getChatRooms();
            for (int i = 0; i < availChatRooms.size(); i++) {
                List<String> chatRoomMemberIds = new ArrayList<>();
                for (int j = 0; j < availChatRooms.get(i).getMembers().size(); j++) {
                    chatRoomMemberIds.add(availChatRooms.get(i).getMembers().get(j)._id);
                }

                Collections.sort(chatRoomMemberIds);
                if (Arrays.equals(memberIds.toArray(), chatRoomMemberIds.toArray())) {
                    changeToMessagingActivity(availChatRooms.get(i));
                    return;
                }
            }

            // if there is no room already, create another
            chatRoomViewModel.createNewChatRoom(memberIds, new ChatRoomViewModel.ApiCallHandler() {
                @Override
                public void onSuccess() {
                    updateChatRoomUI();
                    chatRoomAdapter.notifyItemInserted(0);
                    chatRoomAdapter.notifyItemRangeChanged(0, chatRoomViewModel.getChatRooms().size());
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(InboxActivity.this, "Unable to create the chat room, please try again", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void updateFriendUI(AddNewChatRoomPopupBinding popupBinding) {
        if(friendList.size() > 0) {
            popupBinding.imageNoFriendFound.setVisibility(View.GONE);
            popupBinding.rvFriends.setVisibility(View.VISIBLE);

        } else {
            popupBinding.imageNoFriendFound.setVisibility(View.VISIBLE);
            popupBinding.rvFriends.setVisibility(View.GONE);
        }
    }

    private void changeToMessagingActivity(ChatRoom chatRoom) {
        Intent messagingIntent = new Intent(InboxActivity.this, MessagingActivity.class);
        messagingIntent.putExtra("chatRoomId", chatRoom.get_id());

        // if the chat room is a "GROUP" type, it should naturally have title and imagePath
        // the checking is for a two-person chat room

        // put the chat room title
        if (chatRoom.getTitle() == null || chatRoom.getTitle().isEmpty()) {
            String otherUserName = null;
            String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
            // get the user that is different from the current user to take name
            for (int i = 0; i < chatRoom.getMembers().size(); i++) {
                if (!Objects.equals(chatRoom.getMembers().get(i).get_id(), userId)) {
                    otherUserName = chatRoom.getMembers().get(i).getName();
                    break;
                }
            }
            messagingIntent.putExtra("chatRoomTitle", otherUserName);
        } else messagingIntent.putExtra("chatRoomTitle", chatRoom.getTitle());

        // put the chat room image
        if (chatRoom.getLogoPath() != null && !chatRoom.getLogoPath().isEmpty())
            messagingIntent.putExtra("chatRoomImage", chatRoom.getLogoPath());
        else {
            String imagePath = null;
            String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
            // get the first user that is different from the current user to take the image
            for (int i = 0; i < chatRoom.getMembers().size(); i++) {
                if (!Objects.equals(chatRoom.getMembers().get(i).get_id(), userId)) {
                    imagePath = chatRoom.getMembers().get(i).getImageProfilePath();
                    break;
                }
            }

            messagingIntent.putExtra("chatRoomImage", chatRoom.getLogoPath());
        }

        startActivity(messagingIntent);
    }

    private void updateChatRoomUI()
    {
        if(chatRoomViewModel.getChatRooms().size() > 0)
        {
            binding.emptyChatsContainer.setVisibility(View.GONE);
            binding.rvInbox.setVisibility(View.VISIBLE);

        }
        else
        {
            binding.emptyChatsContainer.setVisibility(View.VISIBLE);
            binding.rvInbox.setVisibility(View.GONE);
        }
    }
}