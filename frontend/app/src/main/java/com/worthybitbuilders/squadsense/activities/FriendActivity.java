package com.worthybitbuilders.squadsense.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.adapters.FriendItemAdapter;
import com.worthybitbuilders.squadsense.adapters.SearchingFriendAdapter;
import com.worthybitbuilders.squadsense.databinding.ActivityFriendBinding;
import com.worthybitbuilders.squadsense.databinding.PopupInviteByEmailBinding;
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
            public void OnItemClick(int position) {}

            @Override
            public void OnMoreOptionsClick(int position) {}
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
        final Dialog dialog = new Dialog(FriendActivity.this);
        PopupInviteByEmailBinding popupInviteByEmailBinding = PopupInviteByEmailBinding.inflate(getLayoutInflater());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(popupInviteByEmailBinding.getRoot());
        popupInviteByEmailBinding.rvSearchFriend.setLayoutManager(new LinearLayoutManager(FriendActivity.this));
        List<UserModel> listAllUser = new ArrayList<>();
        List<UserModel> listSearchingUser = new ArrayList<>();
        SearchingFriendAdapter searchingFriendAdapter = new SearchingFriendAdapter(listSearchingUser);

        searchingFriendAdapter.setOnClickItemFriend(new SearchingFriendAdapter.ClickHandler() {
            @Override
            public void onClick(int position) {
                popupInviteByEmailBinding.inputEmail.setText(listSearchingUser.get(position).getEmail());
            }
        });
        userViewModel.getAllUser(new UserViewModel.CallListUserHandlers() {
            @Override
            public void onSuccess(List<UserModel> dataUsers) {
                String userId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
                dataUsers.forEach(user -> {
                    if(!user.getId().equals(userId)) listAllUser.add(user);
                });
            }

            @Override
            public void onFailure(String message) {
                ToastUtils.showToastError(FriendActivity.this, message, Toast.LENGTH_SHORT);
            }
        });

        popupInviteByEmailBinding.inputEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String toSearch = popupInviteByEmailBinding.inputEmail.getText().toString();
                listSearchingUser.clear();
                if(!toSearch.isEmpty())
                {
                    Drawable[] drawables = popupInviteByEmailBinding.inputEmail.getCompoundDrawables();
                    Drawable clearDrawable = ContextCompat.getDrawable(FriendActivity.this, R.drawable.ic_close);
                    popupInviteByEmailBinding.inputEmail.setCompoundDrawablesRelativeWithIntrinsicBounds(drawables[0], drawables[1], clearDrawable, drawables[3]);

                    listAllUser.forEach(user -> {
                        if(user.getEmail().startsWith(toSearch)) listSearchingUser.add(user);
                    });
                }
                else
                {
                    Drawable[] drawables = popupInviteByEmailBinding.inputEmail.getCompoundDrawables();
                    popupInviteByEmailBinding.inputEmail.setCompoundDrawablesRelativeWithIntrinsicBounds(drawables[0], drawables[1], null, drawables[3]);
                }

                popupInviteByEmailBinding.rvSearchFriend.setAdapter(searchingFriendAdapter);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        popupInviteByEmailBinding.inputEmail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if(popupInviteByEmailBinding.inputEmail.getCompoundDrawables()[2] == null) return false;
                    if (event.getRawX() >= (popupInviteByEmailBinding.inputEmail.getRight() - popupInviteByEmailBinding.inputEmail.getCompoundDrawables()[2].getBounds().width())) {
                        // Người dùng đã chạm vào nút xóa
                        popupInviteByEmailBinding.inputEmail.setText(""); // Xóa toàn bộ chữ trong EditText
                        return true;
                    }
                }
                return false;
            }
        });


        popupInviteByEmailBinding.btnInvite.setOnClickListener(view -> {
            String receiverEmail = popupInviteByEmailBinding.inputEmail.getText().toString();

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
                            ToastUtils.showToastSuccess(FriendActivity.this, "Request was sent to " + receiverEmail + "!!", Toast.LENGTH_SHORT);
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
        });

        popupInviteByEmailBinding.btnClosePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) FriendActivity.this.getSystemService(FriendActivity.this.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        int halfScreenHeight = screenHeight / 2;
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, halfScreenHeight);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
    }

}

