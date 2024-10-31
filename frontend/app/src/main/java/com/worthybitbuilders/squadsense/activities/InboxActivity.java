package com.worthybitbuilders.squadsense.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.adapters.FriendItemAdapter;
import com.worthybitbuilders.squadsense.adapters.NotificationAdapter;
import com.worthybitbuilders.squadsense.databinding.ActivityInboxBinding;
import com.worthybitbuilders.squadsense.models.ChatRoom;
import com.worthybitbuilders.squadsense.models.UserModel;
import com.worthybitbuilders.squadsense.utils.ActivityUtils;
import com.worthybitbuilders.squadsense.utils.DialogUtils;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.viewmodels.ChatRoomViewModel;
import com.worthybitbuilders.squadsense.viewmodels.FriendViewModel;
import com.worthybitbuilders.squadsense.viewmodels.LoginViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InboxActivity extends AppCompatActivity {
    ActivityInboxBinding binding;
    FriendViewModel friendViewModel;
    ChatRoomViewModel chatRoomViewModel;

    Dialog loadingDialog;
    private FriendItemAdapter friendItemAdapter;
    private List<UserModel> friendList = new ArrayList<>();
    private UserModel selectedFriend = null;
    private final int VIEW_INBOX = 0;
    private final int VIEW_FRIEND = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        binding = ActivityInboxBinding.inflate(getLayoutInflater());
        friendViewModel = new ViewModelProvider(this).get(FriendViewModel.class);
        chatRoomViewModel = new ViewModelProvider(this).get(ChatRoomViewModel.class);

        binding.rvFriends.setLayoutManager(new LinearLayoutManager(this));
        friendItemAdapter = new FriendItemAdapter(this, friendList);
        loadingDialog = DialogUtils.GetLoadingDialog(this);

        loadChatRooms();

        binding.btnAddChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAddChatAction();
            }
        });
        binding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowButtonAddChat();
                LoadView(VIEW_INBOX);
                binding.title.setText("Inbox (0)");
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

    private void loadChatRooms()
    {
        Dialog loadingDialog = DialogUtils.GetLoadingDialog(this);
        loadingDialog.show();
        chatRoomViewModel.getChatRooms(new ChatRoomViewModel.ApiCallHandler() {
            @Override
            public void onSuccess() {
                loadingDialog.dismiss();
                updateUI();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(InboxActivity.this, message, Toast.LENGTH_SHORT).show();
                LoadView(VIEW_FRIEND);
                loadingDialog.dismiss();
            }
        });

        friendItemAdapter.setOnClickListener(new FriendItemAdapter.OnActionCallback() {
            @Override
            public void OnClick(int position) {
                Toast.makeText(InboxActivity.this, "clicked at item " + String.valueOf(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnShowingOption(int position) {
                View friend = binding.rvFriends.getChildAt(position);
                registerForContextMenu(friend);
                InboxActivity.this.openContextMenu(friend);
                selectedFriend = friendList.get(position);
            }
        });
    }

    private void LoadView(int VIEW_TYPE)
    {
        switch (VIEW_TYPE)
        {
            case VIEW_INBOX:
                CloseListFriend();
                ShowListInbox();
                break;
            case VIEW_FRIEND:
                CLoseListInbox();
                ShowListFriend();
                break;
        }
    }

    private void ShowListInbox()
    {
        //check if has inbox here
        binding.defaultInbox.setVisibility(View.GONE);
        binding.rvInbox.setVisibility(View.VISIBLE);
        //else
        binding.defaultInbox.setVisibility(View.VISIBLE);
        binding.rvInbox.setVisibility(View.GONE);
    }

    private void CLoseListInbox()
    {
        binding.defaultInbox.setVisibility(View.GONE);
        binding.rvInbox.setVisibility(View.GONE);
    }


    private void ShowListFriend()
    {
        if(friendList.size() > 0)
        {
            binding.imageNoFriendFound.setVisibility(View.GONE);
            binding.rvFriends.setVisibility(View.VISIBLE);

        }
        else
        {
            binding.imageNoFriendFound.setVisibility(View.VISIBLE);
            binding.rvFriends.setVisibility(View.GONE);
        }
    }

    private void CloseListFriend()
    {
        binding.imageNoFriendFound.setVisibility(View.GONE);
        binding.rvFriends.setVisibility(View.GONE);
    }


    private void ShowButtonAddChat()
    {
        binding.btnClose.setVisibility(View.GONE);
        binding.btnAddChat.setVisibility(View.VISIBLE);
    }

    private void ShowButtonClose()
    {
        binding.btnClose.setVisibility(View.VISIBLE);
        binding.btnAddChat.setVisibility(View.GONE);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        InboxActivity.this.getMenuInflater().inflate(R.menu.friend_item_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_unfriend:
                Toast.makeText(this, "unfriend clicked", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void btnAddChatAction()
    {
        LoadFriends();
        ShowButtonClose();
        binding.title.setText("Friends (" + friendList.size() + ")");
    }
}