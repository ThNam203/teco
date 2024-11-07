package com.worthybitbuilders.squadsense.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.adapters.FriendItemAdapter;
import com.worthybitbuilders.squadsense.databinding.ActivityAddMemberBinding;
import com.worthybitbuilders.squadsense.databinding.ActivityMemberBinding;
import com.worthybitbuilders.squadsense.databinding.AddMemberMoreOptionsBinding;
import com.worthybitbuilders.squadsense.models.UserModel;
import com.worthybitbuilders.squadsense.models.board_models.ProjectModel;
import com.worthybitbuilders.squadsense.utils.DialogUtils;
import com.worthybitbuilders.squadsense.utils.EventChecker;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.utils.ToastUtils;
import com.worthybitbuilders.squadsense.viewmodels.FriendViewModel;
import com.worthybitbuilders.squadsense.viewmodels.ProjectActivityViewModel;
import com.worthybitbuilders.squadsense.viewmodels.UserViewModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AddMemberActivity extends AppCompatActivity {

    ActivityAddMemberBinding binding;
    List<UserModel> listFriend = new ArrayList<>();
    List<UserModel> listMember = new ArrayList<>();

    FriendViewModel friendViewModel;
    ProjectActivityViewModel projectActivityViewModel;
    UserViewModel userViewModel;
    FriendItemAdapter friendItemAdapter;
    UserModel selectedFriend = null;
    EventChecker eventChecker = new EventChecker();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityAddMemberBinding.inflate(getLayoutInflater());
        projectActivityViewModel = new ViewModelProvider(this).get(ProjectActivityViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        friendViewModel = new ViewModelProvider(this).get(FriendViewModel.class);
        friendItemAdapter = new FriendItemAdapter(listFriend);

        binding.rvFriends.setLayoutManager(new LinearLayoutManager(AddMemberActivity.this));

        LoadData();

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddMemberActivity.this.onBackPressed();
            }
        });

        setContentView(binding.getRoot());
    }

    private void LoadData()
    {
        Dialog loadingDialog = DialogUtils.GetLoadingDialog(AddMemberActivity.this);
        loadingDialog.show();
        eventChecker.setActionWhenComplete(new EventChecker.CompleteCallback() {
            @Override
            public void Action() {
                loadingDialog.dismiss();
            }
        });

        int LOAD_MEMBER_CODE = eventChecker.addEventStatusAndGetCode();
        String projectId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.CURRENT_PROJECT_ID);
        if(!projectId.isEmpty())
        {
            projectActivityViewModel.getMember(projectId, new ProjectActivityViewModel.ApiCallMemberHandlers() {
                @Override
                public void onSuccess(List<UserModel> listMemberData) {
                    listMember.clear();
                    listMember.addAll(listMemberData);
                    LoadFriendData();
                    eventChecker.markEventAsCompleteAndDoActionIfNeeded(LOAD_MEMBER_CODE);
                }

                @Override
                public void onFailure(String message) {
                    ToastUtils.showToastError(AddMemberActivity.this, message, Toast.LENGTH_SHORT);
                    eventChecker.markEventAsCompleteAndDoActionIfNeeded(LOAD_MEMBER_CODE);
                }
            });

        }
    }

    private void LoadFriendData()
    {
        int LOAD_FRIEND_CODE = eventChecker.addEventStatusAndGetCode();
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        friendViewModel.getFriendById(userId, new FriendViewModel.getFriendCallback() {
            @Override
            public void onSuccess(List<UserModel> friends) {
                List<String> memberIds = new ArrayList<>();
                listMember.forEach(member -> memberIds.add(member.getId()));

                listFriend.clear();
                friends.forEach(friend -> {
                    if(!memberIds.contains(friend.getId()))
                    {
                        listFriend.add(friend);
                    }
                });
                listFriend.sort(Comparator.comparing(UserModel::getName));

                binding.rvFriends.setAdapter(friendItemAdapter);
                LoadListFriendView();
                eventChecker.markEventAsCompleteAndDoActionIfNeeded(LOAD_FRIEND_CODE);
            }

            @Override
            public void onFailure(String message) {
                ToastUtils.showToastError(AddMemberActivity.this, message, Toast.LENGTH_SHORT);
                eventChecker.markEventAsCompleteAndDoActionIfNeeded(LOAD_FRIEND_CODE);
            }
        });

        friendItemAdapter.setOnClickListener(new FriendItemAdapter.OnActionCallback() {
            @Override
            public void OnMoreOptionsClick(int position) {
                View anchor = binding.rvFriends.getLayoutManager().findViewByPosition(position);
                selectedFriend = listFriend.get(position);
                showAddMemberOptions(anchor);
            }
        });
    }

    private void LoadListFriendView()
    {
        if(listFriend.size() > 0)
        {
            binding.screenNoFriendFound.setVisibility(View.GONE);
            binding.rvFriends.setVisibility(View.VISIBLE);
        }
        else
        {
            binding.screenNoFriendFound.setVisibility(View.VISIBLE);
            binding.rvFriends.setVisibility(View.GONE);
        }
    }

    private void showAddMemberOptions(View anchor)
    {
        AddMemberMoreOptionsBinding addMemberMoreOptionsBinding = AddMemberMoreOptionsBinding.inflate(getLayoutInflater());
        PopupWindow popupWindow = new PopupWindow(addMemberMoreOptionsBinding.getRoot(), LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setElevation(50);

        addMemberMoreOptionsBinding.btnAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String receiverEmail = selectedFriend.getEmail();

                userViewModel.getUserByEmail(receiverEmail, new UserViewModel.UserCallback() {
                    @Override
                    public void onSuccess(UserModel receiver) {
                        projectActivityViewModel.requestMemberToJoinProject(SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.CURRENT_PROJECT_ID), receiver.getId(), new ProjectActivityViewModel.ApiCallHandlers() {
                            @Override
                            public void onSuccess() {
                                ToastUtils.showToastSuccess(AddMemberActivity.this, "Request was sent!", Toast.LENGTH_SHORT);
                                popupWindow.dismiss();
                            }

                            @Override
                            public void onFailure(String message) {
                                ToastUtils.showToastError(AddMemberActivity.this, message, Toast.LENGTH_SHORT);
                            }
                        });
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtils.showToastError(AddMemberActivity.this, message, Toast.LENGTH_SHORT);
                    }
                });
            }
        });

        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        int xOffset = anchor.getWidth(); // Offset from the right edge of the anchor view
        int yOffset = - anchor.getHeight() / 2;
        popupWindow.showAsDropDown(anchor, xOffset, yOffset);
    }
}