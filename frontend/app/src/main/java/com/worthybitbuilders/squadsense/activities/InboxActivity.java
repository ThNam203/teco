package com.worthybitbuilders.squadsense.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.adapters.BoardItemOwnerAdapter;
import com.worthybitbuilders.squadsense.adapters.ChatRoomAdapter;
import com.worthybitbuilders.squadsense.adapters.FriendItemAdapter;
import com.worthybitbuilders.squadsense.databinding.ActivityInboxBinding;
import com.worthybitbuilders.squadsense.databinding.AddNewChatRoomPopupBinding;
import com.worthybitbuilders.squadsense.databinding.AddNewGroupChatPopupBinding;
import com.worthybitbuilders.squadsense.databinding.InboxMoreOptionsBinding;
import com.worthybitbuilders.squadsense.models.ChatRoom;
import com.worthybitbuilders.squadsense.models.UserModel;
import com.worthybitbuilders.squadsense.utils.DialogUtils;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.utils.ToastUtils;
import com.worthybitbuilders.squadsense.viewmodels.ChatRoomViewModel;
import com.worthybitbuilders.squadsense.viewmodels.FriendViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class InboxActivity extends AppCompatActivity {
    ActivityInboxBinding binding;
    FriendViewModel friendViewModel;
    ChatRoomViewModel chatRoomViewModel;
    Dialog loadingDialog;
    private FriendItemAdapter friendItemAdapter;

    // TODO: replace this adapter by something similar
    BoardItemOwnerAdapter createGroupMemberAdapter;
    List<UserModel> createGroupMembers = new ArrayList<>();
    private FriendItemAdapter createGroupFriendAdapter;
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
        friendItemAdapter = new FriendItemAdapter(friendList, false);
        createGroupFriendAdapter = new FriendItemAdapter(friendList, false);
        createGroupMemberAdapter = new BoardItemOwnerAdapter(createGroupMembers, false);

        chatRoomAdapter = new ChatRoomAdapter(chatRoomViewModel.getChatRooms(), this::changeToMessagingActivity);
        binding.rvInbox.setAdapter(chatRoomAdapter);

        binding.btnMore.setOnClickListener(this::showInboxMoreOptions);
        binding.btnBack.setOnClickListener(view -> InboxActivity.super.onBackPressed());
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadChatRooms();
    }

    private void showAddChatRoomPopup() {
        final Dialog dialog = new Dialog(InboxActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        AddNewChatRoomPopupBinding addNewChatRoomPopupBinding = AddNewChatRoomPopupBinding.inflate(getLayoutInflater());
        dialog.setContentView(addNewChatRoomPopupBinding.getRoot());

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();

        setUpAddNewChatRoom(addNewChatRoomPopupBinding, dialog);
        addNewChatRoomPopupBinding.btnBack.setOnClickListener(view -> dialog.dismiss());
    }

    private void loadChatRooms()
    {
        chatRoomViewModel.getChatRoomsRemotely(new ChatRoomViewModel.ApiCallHandler() {
            @Override
            public void onSuccess() {
                loadingDialog.dismiss();
                chatRoomAdapter.notifyDataSetChanged();
                updateChatRoomUI();
            }

            @Override
            public void onFailure(String message) {
                ToastUtils.showToastError(InboxActivity.this, message, Toast.LENGTH_SHORT);
                loadingDialog.dismiss();
            }
        });
    }

    private void setUpAddNewChatRoom(AddNewChatRoomPopupBinding popupBinding, Dialog popupDialog)
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
                ToastUtils.showToastError(InboxActivity.this, message, Toast.LENGTH_SHORT);
                updateFriendUI(popupBinding);
                loadingDialog.dismiss();
            }
        });

        friendItemAdapter.setOnClickListener(new FriendItemAdapter.OnActionCallback() {
            @Override
            public void OnItemClick(int position) {
                popupDialog.dismiss();

                List<String> memberIds = new ArrayList<>();
                memberIds.add(friendList.get(position).getId());
                memberIds.add(userId);
                Collections.sort(memberIds);

                // check if there is already a chat room with 2 user
                List<ChatRoom> availChatRooms = chatRoomViewModel.getChatRooms();
                for (int i = 0; i < availChatRooms.size(); i++) {
                    // skip group chats
                    if (availChatRooms.get(i).isGroup()) continue;
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
                chatRoomViewModel.createNewChatRoom(memberIds, false, "", new ChatRoomViewModel.ApiCallHandler() {
                    @Override
                    public void onSuccess() {
                        updateChatRoomUI();
                        chatRoomAdapter.notifyItemInserted(0);
                        chatRoomAdapter.notifyItemRangeChanged(0, chatRoomViewModel.getChatRooms().size());
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtils.showToastError(InboxActivity.this, message, Toast.LENGTH_SHORT);
                    }
                });
            }

            @Override
            public void OnMoreOptionsClick(int position) {

            }
        });
    }

    private void setUpAddNewGroupChatRoom(AddNewGroupChatPopupBinding popupBinding, Dialog popupDialog)
    {
        loadingDialog.show();
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        friendViewModel.getFriendById(userId, new FriendViewModel.getFriendCallback() {
            @Override
            public void onSuccess(List<UserModel> friends) {
                friendList.clear();
                friendList.addAll(friends);
                popupBinding.rvFriends.setLayoutManager(new LinearLayoutManager(InboxActivity.this));
                popupBinding.rvFriends.setAdapter(createGroupFriendAdapter);

                popupBinding.rvGroupMembers.setLayoutManager(new LinearLayoutManager(InboxActivity.this, LinearLayoutManager.HORIZONTAL, false));
                popupBinding.rvGroupMembers.setAdapter(createGroupMemberAdapter);

                if(!friendList.isEmpty()) {
                    popupBinding.imageNoFriendFound.setVisibility(View.GONE);
                    popupBinding.rvFriends.setVisibility(View.VISIBLE);
                    popupBinding.containerGroupName.setVisibility(View.VISIBLE);
                    popupBinding.containerGroupMembers.setVisibility(View.VISIBLE);
                } else {
                    popupBinding.imageNoFriendFound.setVisibility(View.VISIBLE);
                    popupBinding.rvFriends.setVisibility(View.GONE);
                    popupBinding.containerGroupName.setVisibility(View.GONE);
                    popupBinding.containerGroupMembers.setVisibility(View.GONE);
                }

                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(String message) {
                ToastUtils.showToastError(InboxActivity.this, message, Toast.LENGTH_SHORT);
                popupDialog.dismiss();
                loadingDialog.dismiss();
            }
        });

        createGroupFriendAdapter.setOnClickListener(new FriendItemAdapter.OnActionCallback() {
            @Override
            public void OnItemClick(int position) {
                if (createGroupMembers.contains(friendList.get(position))) {
                    createGroupMembers.remove(friendList.get(position));
                    createGroupMemberAdapter.notifyItemRemoved(position);
                    createGroupMemberAdapter.notifyItemRangeChanged(position, createGroupMembers.size());
                } else {
                    createGroupMembers.add(friendList.get(position));
                    createGroupMemberAdapter.notifyItemInserted(position);
                }
            }

            @Override
            public void OnMoreOptionsClick(int position) {}
        });

        createGroupMemberAdapter.setOnClickListener(position -> {
            createGroupMembers.remove(position);
            createGroupMemberAdapter.notifyItemRemoved(position);
            createGroupMemberAdapter.notifyItemRangeChanged(position, createGroupMembers.size());
        });

        popupBinding.btnAddGroup.setOnClickListener((view) -> {
            String groupName = popupBinding.etGroupName.getText().toString();
            if (groupName.isEmpty()) {
                ToastUtils.showToastError(InboxActivity.this, "Enter your group name", Toast.LENGTH_SHORT);
                return;
            }

            ArrayList<String> memberIds = new ArrayList<>();
            memberIds.add(userId);
            for (int i = 0; i < createGroupMembers.size(); i++) memberIds.add(createGroupMembers.get(i).getId());
            loadingDialog.show();

            chatRoomViewModel.createNewChatRoom(memberIds, true, groupName, new ChatRoomViewModel.ApiCallHandler() {
                @Override
                public void onSuccess() {
                    updateChatRoomUI();
                    chatRoomAdapter.notifyItemInserted(0);
                    chatRoomAdapter.notifyItemRangeChanged(0, chatRoomViewModel.getChatRooms().size());
                    createGroupMembers.clear();
                    popupDialog.dismiss();
                    loadingDialog.dismiss();
                }

                @Override
                public void onFailure(String message) {
                    ToastUtils.showToastError(InboxActivity.this, message, Toast.LENGTH_SHORT);
                    loadingDialog.dismiss();
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
        else if (chatRoom.isGroup()) {
            messagingIntent.putExtra("chatRoomImage", "");
            messagingIntent.putExtra("isGroup", true);
        } else {
            String imagePath = null;
            String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
            // get the first user that is different from the current user to take the image
            for (int i = 0; i < chatRoom.getMembers().size(); i++) {
                if (!Objects.equals(chatRoom.getMembers().get(i).get_id(), userId)) {
                    imagePath = chatRoom.getMembers().get(i).getProfileImagePath();
                    break;
                }
            }

            messagingIntent.putExtra("chatRoomImage", imagePath);
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

    private void showInboxMoreOptions(View anchor) {
        InboxMoreOptionsBinding popupBinding = InboxMoreOptionsBinding.inflate(getLayoutInflater());
        PopupWindow popupWindow = new PopupWindow(popupBinding.getRoot(), LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setElevation(50);

        popupBinding.btnAddChat.setOnClickListener(view -> showAddChatRoomPopup());
        popupBinding.btnCreateGroup.setOnClickListener(view -> showAddGroupChatPopup());

        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(anchor, 0, 0);
    }

    private void showAddGroupChatPopup() {
        final Dialog dialog = new Dialog(InboxActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        AddNewGroupChatPopupBinding popupBinding = AddNewGroupChatPopupBinding.inflate(getLayoutInflater());
        dialog.setContentView(popupBinding.getRoot());

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();

        setUpAddNewGroupChatRoom(popupBinding, dialog);
        popupBinding.btnBack.setOnClickListener(view -> dialog.dismiss());
    }
}