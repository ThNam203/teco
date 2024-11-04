package com.worthybitbuilders.squadsense.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import android.widget.Toast;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.adapters.FriendRequestAdapter;
import com.worthybitbuilders.squadsense.adapters.FriendItemAdapter;
import com.worthybitbuilders.squadsense.databinding.ActivityFriendBinding;
import com.worthybitbuilders.squadsense.models.FriendRequest;
import com.worthybitbuilders.squadsense.models.Notification;
import com.worthybitbuilders.squadsense.models.UserModel;
import com.worthybitbuilders.squadsense.utils.ActivityUtils;
import com.worthybitbuilders.squadsense.utils.DialogUtils;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.utils.ToastUtils;
import com.worthybitbuilders.squadsense.viewmodels.FriendViewModel;
import com.worthybitbuilders.squadsense.viewmodels.NotificationViewModel;
import com.worthybitbuilders.squadsense.viewmodels.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class FriendActivity extends AppCompatActivity {

    ActivityFriendBinding binding;

    NotificationViewModel notificationViewModel;
    FriendViewModel friendViewModel;
    UserViewModel userViewModel;
    FriendItemAdapter friendItemAdapter;
    List<UserModel> listFriend = new ArrayList<>();

    Dialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityFriendBinding.inflate(getLayoutInflater());
        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        friendViewModel = new ViewModelProvider(this).get(FriendViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        loadingDialog = DialogUtils.GetLoadingDialog(this);

        binding.rvFriends.setLayoutManager(new LinearLayoutManager(this));
        friendItemAdapter = new FriendItemAdapter(listFriend);

        LoadlistFriend();

        binding.btnFriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityUtils.switchToActivity(FriendActivity.this, FriendRequestActivity.class);
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FriendActivity.this.onBackPressed();
            }
        });

        binding.btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_invite_showDialog();
            }
        });

        setContentView(binding.getRoot());
    }

    private void LoadlistFriend()
    {
        loadingDialog.show();
        String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        friendViewModel.getFriendById(userId, new FriendViewModel.getFriendCallback() {
            @Override
            public void onSuccess(List<UserModel> friends) {
                listFriend.clear();
                listFriend.addAll(friends);

                binding.rvFriends.setAdapter(friendItemAdapter);
                LoadListFriendView();
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(String message) {
                ToastUtils.showToastError(FriendActivity.this, message, Toast.LENGTH_SHORT);
                loadingDialog.dismiss();
            }
        });

        friendItemAdapter.setOnClickListener(new FriendItemAdapter.OnActionCallback() {
            @Override
            public void OnClick(int position) {

            }
        });
    }

    private void LoadListFriendView()
    {
        if(listFriend.size() > 0)
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

    private void btn_invite_showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_btn_invite_by_email);

        //Set activity of button in dialog here
        EditText inputEmail = (EditText) dialog.findViewById(R.id.input_email);
        inputEmail.requestFocus();
        AppCompatButton btnInvite = (AppCompatButton) dialog.findViewById(R.id.btn_invite);

        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String receiverEmail = inputEmail.getText().toString();

                if(!friendViewModel.IsValidEmail(receiverEmail))
                {
                    ToastUtils.showToastError(FriendActivity.this, "Invalid email", Toast.LENGTH_SHORT);
                    return;
                }

                userViewModel.getUserByEmail(receiverEmail, new UserViewModel.UserCallback() {
                    @Override
                    public void onSuccess(UserModel user) {
                        friendViewModel.createRequest(SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID), user.getId(), new FriendViewModel.FriendRequestCallback() {
                            @Override
                            public void onSuccess() {
                                ToastUtils.showToastSuccess(FriendActivity.this, "Request is sent to " + receiverEmail + "!", Toast.LENGTH_SHORT);
                            }

                            @Override
                            public void onFailure(String message) {
                                ToastUtils.showToastError(FriendActivity.this, message, Toast.LENGTH_SHORT);
                            }
                        });
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtils.showToastError(FriendActivity.this, message, Toast.LENGTH_SHORT);
                    }
                });
            }
        });
        //

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();

        ImageButton btnClosePopupBtnInvite = (ImageButton) dialog.findViewById(R.id.btn_close_popup);
        btnClosePopupBtnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

}

