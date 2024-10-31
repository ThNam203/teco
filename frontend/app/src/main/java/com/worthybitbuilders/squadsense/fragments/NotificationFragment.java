package com.worthybitbuilders.squadsense.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.activities.FriendRequestActivity;
import com.worthybitbuilders.squadsense.activities.InboxActivity;
import com.worthybitbuilders.squadsense.activities.NotificationSettingActivity;
import com.worthybitbuilders.squadsense.adapters.NotificationAdapter;
import com.worthybitbuilders.squadsense.databinding.FragmentNotificationBinding;
import com.worthybitbuilders.squadsense.models.Notification;
import com.worthybitbuilders.squadsense.models.UserModel;
import com.worthybitbuilders.squadsense.utils.ActivityUtils;
import com.worthybitbuilders.squadsense.utils.Convert;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.utils.ToastUtils;
import com.worthybitbuilders.squadsense.viewmodels.FriendViewModel;
import com.worthybitbuilders.squadsense.viewmodels.NotificationViewModel;
import com.worthybitbuilders.squadsense.viewmodels.UserViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class NotificationFragment extends Fragment {
    private List<Button> buttonList = new ArrayList<>();
    private FragmentNotificationBinding binding;
    private NotificationViewModel notificationViewModel;
    private FriendViewModel friendViewModel;
    private NotificationAdapter notificationAdapter;
    private UserViewModel userViewModel;
    private List<Notification> listNotification = new ArrayList<>(); //work as data to put in recyclerview
    private List<Notification> tempListNotification = new ArrayList<>(); //work as cache, it use to store all notifications got from server

    private Notification selectedNotification;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBinding.inflate(getLayoutInflater());
        friendViewModel = new ViewModelProvider(this).get(FriendViewModel.class);
        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        binding.recyclerviewNotification.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationAdapter = new NotificationAdapter(getContext(), listNotification);

        setupBtnList();

        notificationViewModel.getNotification(SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID), new NotificationViewModel.getNotificationCallback() {
            @Override
            public void onSuccess(List<Notification> notificationsData) {
                for (Notification item : notificationsData) {
                    listNotification.add(item);
                    tempListNotification.add(item);
                }
                binding.recyclerviewNotification.setAdapter(notificationAdapter);
                ReloadNotificationView();
            }

            @Override
            public void onFailure(String message) {
                ToastUtils.showToastError(getContext(), message, Gravity.TOP);
            }
        });

        notificationAdapter.setOnReplyListener(new NotificationAdapter.OnActionCallback() {
            @Override
            public void OnClick(int position) {
                ActivityUtils.switchToActivity(getContext(), FriendRequestActivity.class);
            }

            @Override
            public void OnShowingOption(int position) {
                View notification = binding.recyclerviewNotification.getChildAt(position);
                registerForContextMenu(notification);
                getActivity().openContextMenu(notification);
                selectedNotification = listNotification.get(position);
            }
        });

        binding.btnAllNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listNotification.clear();

                //get all notification
                for (Notification item : tempListNotification)
                {
                    listNotification.add(item);
                }

                //reload view of list
                binding.recyclerviewNotification.setAdapter(notificationAdapter);
                ReloadNotificationView();

                //change color button selected
                for (Button btn : buttonList)
                {
                    btn.setSelected(false);
                }
                binding.btnAllNotification.setSelected(true);
            }
        });

        binding.btnTodayNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listNotification.clear();

                //get today notification
                String dateNow = Convert.DateToString(new Date(), Convert.Pattern.DAY_MONTH_YEAR);
                for (Notification item : tempListNotification)
                {
                    if(item.getTimeCreatedDMY().equals(dateNow))
                    {
                        listNotification.add(item);
                    }
                }

                //reload view of list
                binding.recyclerviewNotification.setAdapter(notificationAdapter);
                ReloadNotificationView();

                //change color button selected
                for (Button btn : buttonList)
                {
                    btn.setSelected(false);
                }
                binding.btnTodayNotification.setSelected(true);
            }
        });

        binding.btnMentionedNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listNotification.clear();

                //reload view of list
                binding.recyclerviewNotification.setAdapter(notificationAdapter);
                ReloadNotificationView();

                //change color button selected
                for (Button btn : buttonList)
                {
                    btn.setSelected(false);
                }
                binding.btnMentionedNotification.setSelected(true);
            }
        });

        //set onclick buttons here
        binding.btnInviteMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_invite_showDialog();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.notification_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option_setting:
                ActivityUtils.switchToActivity(getContext(), NotificationSettingActivity.class);
                return true;
            case R.id.option_delete:
                DeleteNotification(selectedNotification);
                listNotification.remove(selectedNotification);
                tempListNotification.remove(selectedNotification);
                binding.recyclerviewNotification.setAdapter(notificationAdapter);
                ReloadNotificationView();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void ReloadNotificationView()
    {
        if(listNotification.size() > 0)
        {
            binding.defaultviewNotification.setVisibility(View.GONE);
            binding.recyclerviewNotification.setVisibility(View.VISIBLE);
        }
        else
        {
            binding.defaultviewNotification.setVisibility(View.VISIBLE);
            binding.recyclerviewNotification.setVisibility(View.GONE);
        }
    }

    private void DeleteNotification(Notification notification){
        if(notification.getNotificationType().equals("FriendRequest"))
            DeleteFriendRequest(notification);

        notificationViewModel.deleteNotification(notification.getId(), new NotificationViewModel.deleteNotificationCallback() {
            @Override
            public void onSuccess() {
                ToastUtils.showToastSuccess(getContext(), "notification deleted", Gravity.TOP);
            }

            @Override
            public void onFailure(String message) {
                ToastUtils.showToastError(getContext(), message, Gravity.TOP);
            }
        });
    }

    //define function here
    private void btn_invite_showDialog() {
        final Dialog dialog = new Dialog(getActivity());
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
                    ToastUtils.showToastError(getContext(), "Invalid email", Gravity.TOP);
                    return;
                }

                userViewModel.getUserByEmail(receiverEmail, new UserViewModel.UserCallback() {
                    @Override
                    public void onSuccess(UserModel user) {
                        friendViewModel.createRequest(SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID), user.getId(), new FriendViewModel.FriendRequestCallback() {
                            @Override
                            public void onSuccess() {
                                ToastUtils.showToastSuccess(getContext(), "request was sent to " + receiverEmail + "!!", Gravity.TOP);
                            }

                            @Override
                            public void onFailure(String message) {
                                ToastUtils.showToastError(getContext(), message, Gravity.TOP);
                            }
                        });
                    }

                    @Override
                    public void onFailure(String message) {
                        ToastUtils.showToastError(getContext(), message, Gravity.TOP);
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

    private void setupBtnList()
    {
        //Add button to list buttons in horizontal scrollview
        buttonList.add(binding.btnAllNotification);
        buttonList.add(binding.btnMentionedNotification);
        buttonList.add(binding.btnTodayNotification);

        binding.btnAllNotification.setSelected(true);
    }

    private void DeleteFriendRequest(Notification notificationOfFriendRequest){
        String replierId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        String requestSender = notificationOfFriendRequest.getSenderId();
        friendViewModel.reply(replierId, requestSender, "Deny", new FriendViewModel.FriendRequestCallback() {
            @Override
            public void onSuccess() {
                ToastUtils.showToastSuccess(getContext(), "you have just denied the request!!", Gravity.TOP);
            }

            @Override
            public void onFailure(String message) {
                ToastUtils.showToastError(getContext(), message, Gravity.TOP);
            }
        });
    }
}