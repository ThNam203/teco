package com.worthybitbuilders.squadsense.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.adapters.FriendItemAdapter;
import com.worthybitbuilders.squadsense.adapters.MemberAdapter;
import com.worthybitbuilders.squadsense.databinding.ActivityMemberBinding;
import com.worthybitbuilders.squadsense.databinding.AddMemberMoreOptionsBinding;
import com.worthybitbuilders.squadsense.databinding.MemberMoreOptionsBinding;
import com.worthybitbuilders.squadsense.databinding.ProjectMoreOptionsBinding;
import com.worthybitbuilders.squadsense.models.UserModel;
import com.worthybitbuilders.squadsense.models.board_models.ProjectModel;
import com.worthybitbuilders.squadsense.utils.ActivityUtils;
import com.worthybitbuilders.squadsense.utils.Checking;
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

public class MemberActivity extends AppCompatActivity {
    ActivityMemberBinding binding;
    MemberAdapter memberAdapter;
    List<UserModel> listMember = new ArrayList<>();
    List<String> listAdminId = new ArrayList<>();

    String creatorId;
    ProjectActivityViewModel projectActivityViewModel;

    EventChecker eventChecker;

    UserModel seletedMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityMemberBinding.inflate(getLayoutInflater());
        projectActivityViewModel = new ViewModelProvider(this).get(ProjectActivityViewModel.class);
        eventChecker = new EventChecker();
        binding.rvMembers.setLayoutManager(new LinearLayoutManager(this));
        memberAdapter = new MemberAdapter(listMember, listAdminId);

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MemberActivity.this.onBackPressed();
            }
        });

        binding.btnAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityUtils.switchToActivity(MemberActivity.this, AddMemberActivity.class);
            }
        });


        setContentView(binding.getRoot());
    }

    @Override
    protected void onStart() {
        super.onStart();
        LoadData();
    }

    private void LoadMemberData()
    {
        int LOAD_MEMBER_CODE = eventChecker.addEventStatusAndGetCode();
        String projectId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.CURRENT_PROJECT_ID);
        projectActivityViewModel.getMember(projectId, new ProjectActivityViewModel.ApiCallMemberHandlers() {
            @Override
            public void onSuccess(List<UserModel> listMemberData) {
                listMember.clear();
                listMember.addAll(listMemberData);
                listMember.sort(Comparator.comparing(UserModel::getName));
                creatorId = projectActivityViewModel.getProjectModel().getCreatorId();
                memberAdapter.setCreatorId(creatorId);
                LoadListMemberView();
                eventChecker.markEventAsCompleteAndDoActionIfNeeded(LOAD_MEMBER_CODE);
            }

            @Override
            public void onFailure(String message) {
                ToastUtils.showToastError(MemberActivity.this, "Can not load member of this project", Toast.LENGTH_LONG);
                eventChecker.markEventAsCompleteAndDoActionIfNeeded(LOAD_MEMBER_CODE);
            }
        });
        memberAdapter.setOnClickListener(new MemberAdapter.OnActionCallback() {
            @Override
            public void OnClick(int position) {

            }

            @Override
            public void OnMoreOptionsClick(int position) {
                View anchor = binding.rvMembers.getLayoutManager().findViewByPosition(position);
                seletedMember = listMember.get(position);
                showMemberOptions(anchor);
            }
        });
    }

    private void LoadData()
    {
        Dialog loadingDialog = DialogUtils.GetLoadingDialog(MemberActivity.this);
        loadingDialog.show();
        eventChecker.setActionWhenComplete(new EventChecker.CompleteCallback() {
            @Override
            public void Action() {
                loadingDialog.dismiss();
            }
        });

        int LOAD_ADMINID_CODE = eventChecker.addEventStatusAndGetCode();
        String projectId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.CURRENT_PROJECT_ID);
        if(!projectId.isEmpty())
        {
            projectActivityViewModel.getProjectById(projectId, new ProjectActivityViewModel.ApiCallHandlers() {
                @Override
                public void onSuccess() {
                    listAdminId.addAll(projectActivityViewModel.getProjectModel().getAdminIds());
                    LoadMemberData();
                    eventChecker.markEventAsCompleteAndDoActionIfNeeded(LOAD_ADMINID_CODE);
                }

                @Override
                public void onFailure(String message) {
                    ToastUtils.showToastError(MemberActivity.this, message, Toast.LENGTH_SHORT);
                    eventChecker.markEventAsCompleteAndDoActionIfNeeded(LOAD_ADMINID_CODE);
                }
            });

        }
    }

    private void LoadListMemberView()
    {
        memberAdapter.notifyDataSetChanged();
        binding.rvMembers.setAdapter(memberAdapter);

        if(listMember.size() > 0)
        {
            binding.rvMembers.setVisibility(View.VISIBLE);
            binding.screenNoMemberFound.setVisibility(View.GONE);
        }
        else
        {
            binding.rvMembers.setVisibility(View.GONE);
            binding.screenNoMemberFound.setVisibility(View.VISIBLE);
        }
    }

    private void showMemberOptions(View anchor) {
        MemberMoreOptionsBinding memberMoreOptionsBinding = MemberMoreOptionsBinding.inflate(getLayoutInflater());
        PopupWindow popupWindow = new PopupWindow(memberMoreOptionsBinding.getRoot(), LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setElevation(50);

        //show view depended on role of user
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        String creatorId = projectActivityViewModel.getProjectModel().getCreatorId();

        if (seletedMember.getId().equals(userId))
        {
            memberMoreOptionsBinding.btnProfileInfo.setVisibility(View.VISIBLE);
            memberMoreOptionsBinding.btnMakeAdmin.setVisibility(View.GONE);
            memberMoreOptionsBinding.btnDeleteMember.setVisibility(View.GONE);
            memberMoreOptionsBinding.btnChangeToMember.setVisibility(View.GONE);
        }
        else if(creatorId.equals(userId))
            showMemberOptionsViewFor(Role.CREATOR, memberMoreOptionsBinding);
        else if(listAdminId.contains(userId))
            showMemberOptionsViewFor(Role.ADMIN, memberMoreOptionsBinding);
        else
            showMemberOptionsViewFor(Role.MEMBER, memberMoreOptionsBinding);

        memberMoreOptionsBinding.btnDeleteMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String projectId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.CURRENT_PROJECT_ID);
                String memberId = seletedMember.getId();
                projectActivityViewModel.deleteMember(projectId, memberId, new ProjectActivityViewModel.ApiCallHandlers() {
                    @Override
                    public void onSuccess() {
                        listMember.remove(seletedMember);
                        LoadListMemberView();
                        popupWindow.dismiss();
                        ToastUtils.showToastSuccess(MemberActivity.this, "Member deleted", Toast.LENGTH_SHORT);
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtils.showToastError(MemberActivity.this, message, Toast.LENGTH_SHORT);
                    }
                });
            }
        });

        memberMoreOptionsBinding.btnMakeAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String titleComfirmDialog = "Make admin";
                String contentComfirmDialog = "Are you sure you want to change the role to Admin for this user?";
                DialogUtils.showConfirmDialogYesNo(MemberActivity.this, titleComfirmDialog, contentComfirmDialog, new DialogUtils.ConfirmAction() {
                    @Override
                    public void onAcceptToDo(Dialog thisDialog) {
                        String projectId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.CURRENT_PROJECT_ID);
                        String memberId = seletedMember.getId();
                        projectActivityViewModel.makeAdmin(projectId, memberId, new ProjectActivityViewModel.ApiCallHandlers() {
                            @Override
                            public void onSuccess() {
                                ToastUtils.showToastSuccess(MemberActivity.this, seletedMember.getName() + " has been successfully changed to the Admin", Toast.LENGTH_SHORT);
                                LoadData();
                            }

                            @Override
                            public void onFailure(String message) {
                                ToastUtils.showToastError(MemberActivity.this, message, Toast.LENGTH_SHORT);
                            }
                        });
                        thisDialog.dismiss();
                    }

                    @Override
                    public void onCancel(Dialog thisDialog) {
                        thisDialog.dismiss();
                    }
                });
                popupWindow.dismiss();
            }
        });

        memberMoreOptionsBinding.btnChangeToMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String titleComfirmDialog = "Change to member";
                String contentComfirmDialog = "Are you sure you want to change the role to Member for this user?";
                DialogUtils.showConfirmDialogYesNo(MemberActivity.this, titleComfirmDialog, contentComfirmDialog, new DialogUtils.ConfirmAction() {
                    @Override
                    public void onAcceptToDo(Dialog thisDialog) {
                        thisDialog.dismiss();
                        String projectId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.CURRENT_PROJECT_ID);
                        String adminId = seletedMember.getId();
                        projectActivityViewModel.changeAdminToMember(projectId, adminId, new ProjectActivityViewModel.ApiCallHandlers() {
                            @Override
                            public void onSuccess() {
                                ToastUtils.showToastSuccess(MemberActivity.this, seletedMember.getName() + " has been successfully changed to the Member", Toast.LENGTH_SHORT);
                                LoadData();
                            }

                            @Override
                            public void onFailure(String message) {
                                ToastUtils.showToastError(MemberActivity.this, message, Toast.LENGTH_SHORT);
                            }
                        });
                    }

                    @Override
                    public void onCancel(Dialog thisDialog) {
                        thisDialog.dismiss();

                    }
                });
                popupWindow.dismiss();
            }
        });

        memberMoreOptionsBinding.btnProfileInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();

                if(seletedMember.getId().equals(userId))
                {
                    ActivityUtils.switchToActivity(MemberActivity.this, OpenProfileActivity.class);
                }
                else
                {
                    Intent memberInfoActivityIntent = new Intent(MemberActivity.this, MemberInfoActivity.class);
                    memberInfoActivityIntent.putExtra("memberId", seletedMember.getId());
                    startActivity(memberInfoActivityIntent);
                }
            }
        });

        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        int xOffset = anchor.getWidth(); // Offset from the right edge of the anchor view
        int yOffset = - anchor.getHeight() / 2;
        popupWindow.showAsDropDown(anchor, xOffset, yOffset);
    }

    enum Role{ CREATOR, ADMIN, MEMBER }

    private void showMemberOptionsViewFor(Role role, MemberMoreOptionsBinding memberMoreOptionsBinding)
    {
        memberMoreOptionsBinding.btnDeleteMember.setVisibility(View.VISIBLE);
        memberMoreOptionsBinding.btnProfileInfo.setVisibility(View.VISIBLE);
        memberMoreOptionsBinding.btnMakeAdmin.setVisibility(View.VISIBLE);
        memberMoreOptionsBinding.btnChangeToMember.setVisibility(View.VISIBLE);

        String creatorId = projectActivityViewModel.getProjectModel().getCreatorId();
        if(role == Role.CREATOR)
        {
            if(listAdminId.contains(seletedMember.getId()))
            {
                memberMoreOptionsBinding.btnMakeAdmin.setVisibility(View.GONE);
            }
            else{
                memberMoreOptionsBinding.btnChangeToMember.setVisibility(View.GONE);
            }
        }
        else if(role == Role.ADMIN)
        {
            if(creatorId.equals(seletedMember.getId()))
            {
                memberMoreOptionsBinding.btnDeleteMember.setVisibility(View.GONE);
                memberMoreOptionsBinding.btnMakeAdmin.setVisibility(View.GONE);
                memberMoreOptionsBinding.btnChangeToMember.setVisibility(View.GONE);
            }
            else if(listAdminId.contains(seletedMember.getId()))
            {
                memberMoreOptionsBinding.btnDeleteMember.setVisibility(View.GONE);
                memberMoreOptionsBinding.btnMakeAdmin.setVisibility(View.GONE);
                memberMoreOptionsBinding.btnChangeToMember.setVisibility(View.GONE);
            }
            else
            {
                memberMoreOptionsBinding.btnMakeAdmin.setVisibility(View.GONE);
                memberMoreOptionsBinding.btnChangeToMember.setVisibility(View.GONE);
            }
        }
        else {
            if(creatorId.equals(seletedMember.getId()))
            {
                memberMoreOptionsBinding.btnDeleteMember.setVisibility(View.GONE);
                memberMoreOptionsBinding.btnMakeAdmin.setVisibility(View.GONE);
                memberMoreOptionsBinding.btnChangeToMember.setVisibility(View.GONE);
            }
            else if(listAdminId.contains(seletedMember.getId()))
            {
                memberMoreOptionsBinding.btnDeleteMember.setVisibility(View.GONE);
                memberMoreOptionsBinding.btnMakeAdmin.setVisibility(View.GONE);
                memberMoreOptionsBinding.btnChangeToMember.setVisibility(View.GONE);
            }
            else
            {
                memberMoreOptionsBinding.btnDeleteMember.setVisibility(View.GONE);
                memberMoreOptionsBinding.btnMakeAdmin.setVisibility(View.GONE);
                memberMoreOptionsBinding.btnChangeToMember.setVisibility(View.GONE);
            }
        }
    }
}