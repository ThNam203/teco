package com.worthybitbuilders.squadsense.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
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

        LoadData();

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
                binding.rvMembers.setAdapter(memberAdapter);
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

        //if user is an admin -> show special btn for admin
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        if(listAdminId.contains(userId))
            showMemberOptionsViewFor(Role.ADMIN, memberMoreOptionsBinding);
        else
            showMemberOptionsViewFor(Role.MEMBER, memberMoreOptionsBinding);

        memberMoreOptionsBinding.btnDeleteMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String projectId = projectActivityViewModel.getProjectId();
                String memberId = seletedMember.getId();
                projectActivityViewModel.deleteMember(projectId, memberId, new ProjectActivityViewModel.ApiCallHandlers() {
                    @Override
                    public void onSuccess() {
                        listMember.remove(seletedMember);
                        binding.rvMembers.setAdapter(memberAdapter);
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

            }
        });

        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        int xOffset = anchor.getWidth(); // Offset from the right edge of the anchor view
        int yOffset = - anchor.getHeight() / 2;
        popupWindow.showAsDropDown(anchor, xOffset, yOffset);
    }

    enum Role{ ADMIN, MEMBER }

    private void showMemberOptionsViewFor(Role role, MemberMoreOptionsBinding memberMoreOptionsBinding)
    {
        if(role == Role.ADMIN)
        {
            if(listAdminId.contains(seletedMember.getId()))
            {
                memberMoreOptionsBinding.btnDeleteMember.setVisibility(View.GONE);
                memberMoreOptionsBinding.btnMakeAdmin.setVisibility(View.GONE);
                memberMoreOptionsBinding.btnMemberInfo.setVisibility(View.VISIBLE);
            }
            else
            {
                memberMoreOptionsBinding.btnDeleteMember.setVisibility(View.VISIBLE);
                memberMoreOptionsBinding.btnMakeAdmin.setVisibility(View.VISIBLE);
                memberMoreOptionsBinding.btnMemberInfo.setVisibility(View.VISIBLE);
            }
        }
        else {
            if(listAdminId.contains(seletedMember.getId()))
            {
                memberMoreOptionsBinding.btnDeleteMember.setVisibility(View.GONE);
                memberMoreOptionsBinding.btnMakeAdmin.setVisibility(View.GONE);
                memberMoreOptionsBinding.btnMemberInfo.setVisibility(View.VISIBLE);
            }
            else
            {
                memberMoreOptionsBinding.btnDeleteMember.setVisibility(View.GONE);
                memberMoreOptionsBinding.btnMakeAdmin.setVisibility(View.GONE);
                memberMoreOptionsBinding.btnMemberInfo.setVisibility(View.VISIBLE);
            }
        }
    }
}