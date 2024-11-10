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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.activities.FriendRequestActivity;
import com.worthybitbuilders.squadsense.activities.InboxActivity;
import com.worthybitbuilders.squadsense.activities.MemberActivity;
import com.worthybitbuilders.squadsense.activities.NotificationSettingActivity;
import com.worthybitbuilders.squadsense.adapters.NotificationAdapter;
import com.worthybitbuilders.squadsense.databinding.FragmentNotificationBinding;
import com.worthybitbuilders.squadsense.databinding.MemberMoreOptionsBinding;
import com.worthybitbuilders.squadsense.databinding.NotificationMoreOptionsBinding;
import com.worthybitbuilders.squadsense.models.Notification;
import com.worthybitbuilders.squadsense.models.UserModel;
import com.worthybitbuilders.squadsense.utils.ActivityUtils;
import com.worthybitbuilders.squadsense.utils.Convert;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.utils.ToastUtils;
import com.worthybitbuilders.squadsense.viewmodels.FriendViewModel;
import com.worthybitbuilders.squadsense.viewmodels.NotificationViewModel;
import com.worthybitbuilders.squadsense.viewmodels.ProjectActivityViewModel;
import com.worthybitbuilders.squadsense.viewmodels.UserViewModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class NotificationFragment extends Fragment {
    private List<Button> buttonList = new ArrayList<>();
    private FragmentNotificationBinding binding;
    private NotificationViewModel notificationViewModel;
    private FriendViewModel friendViewModel;
    private ProjectActivityViewModel projectActivityViewModel;
    private NotificationAdapter notificationAdapter;
    private UserViewModel userViewModel;
    private List<Notification> listNotification = new ArrayList<>(); //work as data to put in recyclerview
    private List<Notification> listAllNotification = new ArrayList<>(); //work as cache, it use to store all notifications got from server

    private Notification selectedNotification;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBinding.inflate(getLayoutInflater());
        friendViewModel = new ViewModelProvider(this).get(FriendViewModel.class);
        projectActivityViewModel = new ViewModelProvider(this).get(ProjectActivityViewModel.class);
        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        binding.recyclerviewNotification.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationAdapter = new NotificationAdapter(listNotification);

        setupBtnList();

        notificationViewModel.getNotification(SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID), new NotificationViewModel.getNotificationCallback() {
            @Override
            public void onSuccess(List<Notification> notificationsData) {
                listNotification.addAll(notificationsData);
                listNotification.sort(Comparator.comparing(Notification::getTimeCreated).reversed());
                listAllNotification.addAll(notificationsData);
                binding.recyclerviewNotification.setAdapter(notificationAdapter);
                ReloadNotificationView();
            }

            @Override
            public void onFailure(String message) {
                ToastUtils.showToastError(getContext(), message, Toast.LENGTH_SHORT);
            }
        });

        notificationAdapter.setOnActionListener(new NotificationAdapter.OnActionCallback() {
            @Override
            public void OnClick(int position) {
                ActivityUtils.switchToActivity(getContext(), FriendRequestActivity.class);
            }

            @Override
            public void OnShowingOption(int position) {
                View anchor = binding.recyclerviewNotification.getChildAt(position);
                showNotificationMoreOptions(anchor);
                selectedNotification = listNotification.get(position);
            }
        });

        notificationAdapter.setOnReplyListener(new NotificationAdapter.OnReplyCallback() {
            @Override
            public void OnAccept(int position, String NOTIFICATION_TYPE) {
                String projectId = listNotification.get(position).getLink();
                String senderId = listNotification.get(position).getSenderId();
                String senderName = listNotification.get(position).getTitle();
                switch (NOTIFICATION_TYPE){
                    case "MemberRequest":
                        projectActivityViewModel.replyToJoinProject(projectId, senderId, "Accept", new ProjectActivityViewModel.ApiCallHandlers() {
                            @Override
                            public void onSuccess() {
                                ToastUtils.showToastSuccess(getContext(), "You are added to this project, check it now!", Toast.LENGTH_SHORT);
                                listNotification.remove(position);
                                listAllNotification.remove(position);
                                binding.recyclerviewNotification.setAdapter(notificationAdapter);
                                ReloadNotificationView();
                            }

                            @Override
                            public void onFailure(String message) {
                                ToastUtils.showToastError(getContext(), message, Toast.LENGTH_SHORT);
                            }
                        });
                        break;
                    case "AdminRequest":
                        projectActivityViewModel.replyToAdminRequest(projectId, senderId, "Accept", new ProjectActivityViewModel.ApiCallHandlers() {
                            @Override
                            public void onSuccess() {
                                ToastUtils.showToastSuccess(getContext(), "Request of " + senderName + " was accepted", Toast.LENGTH_SHORT);
                                listNotification.remove(position);
                                listAllNotification.remove(position);
                                binding.recyclerviewNotification.setAdapter(notificationAdapter);
                                ReloadNotificationView();
                            }

                            @Override
                            public void onFailure(String message) {
                                ToastUtils.showToastError(getContext(), message, Toast.LENGTH_SHORT);
                            }
                        });
                        break;
                }

            }

            @Override
            public void OnDeny(int position, String NOTIFICATION_TYPE) {
                String projectId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.CURRENT_PROJECT_ID);
                String senderId = listNotification.get(position).getSenderId();
                switch (NOTIFICATION_TYPE)
                {
                    case "MemberRequest":

                        projectActivityViewModel.replyToJoinProject(projectId, senderId, "Deny", new ProjectActivityViewModel.ApiCallHandlers() {
                            @Override
                            public void onSuccess() {
                                listNotification.remove(position);
                                listAllNotification.remove(position);
                                binding.recyclerviewNotification.setAdapter(notificationAdapter);
                                ReloadNotificationView();
                            }

                            @Override
                            public void onFailure(String message) {
                                ToastUtils.showToastError(getContext(), message, Toast.LENGTH_SHORT);
                            }
                        });
                        break;
                    case "AdminRequest":
                        projectActivityViewModel.replyToAdminRequest(projectId, senderId, "Deny", new ProjectActivityViewModel.ApiCallHandlers() {
                            @Override
                            public void onSuccess() {
                                listNotification.remove(position);
                                listAllNotification.remove(position);
                                binding.recyclerviewNotification.setAdapter(notificationAdapter);
                                ReloadNotificationView();
                            }

                            @Override
                            public void onFailure(String message) {
                                ToastUtils.showToastError(getContext(), message, Toast.LENGTH_SHORT);
                            }
                        });
                        break;
                }
            }
        });

        binding.btnAllNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listNotification.clear();
                listNotification.addAll(listAllNotification);

                //reload view of list
                binding.recyclerviewNotification.setAdapter(notificationAdapter);
                ReloadNotificationView();

                setSelectedBtnInScrollView(binding.btnAllNotification, buttonList);
            }
        });

        binding.btnTodayNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listNotification.clear();

                //get today notification
                String dateNow = Convert.DateToString(new Date(), Convert.Pattern.DAY_MONTH_YEAR);
                for (Notification item : listAllNotification)
                {
                    if(item.getTimeCreatedDMY().equals(dateNow))
                    {
                        listNotification.add(item);
                    }
                }

                //reload view of list
                binding.recyclerviewNotification.setAdapter(notificationAdapter);
                ReloadNotificationView();

                setSelectedBtnInScrollView(binding.btnTodayNotification, buttonList);
            }
        });

        binding.btnMentionedNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listNotification.clear();

                //reload view of list
                binding.recyclerviewNotification.setAdapter(notificationAdapter);
                ReloadNotificationView();

                setSelectedBtnInScrollView(binding.btnMentionedNotification, buttonList);
            }
        });

        return binding.getRoot();
    }

    private void setSelectedBtnInScrollView(Button selectedButton, List<Button> listBtn)
    {
        //change color button unselected selected
        for (Button btn : listBtn)
        {
            btn.setSelected(false);
            int color = getResources().getColor(R.color.primary_word_color, getActivity().getTheme());
            btn.setTextColor(color);
        }

        //change color of selected button
        selectedButton.setSelected(true);
        int color = getResources().getColor(R.color.white, getActivity().getTheme());
        selectedButton.setTextColor(color);
    }


    private void showNotificationMoreOptions(View anchor)
    {
        NotificationMoreOptionsBinding notificationMoreOptionsBinding = NotificationMoreOptionsBinding.inflate(getLayoutInflater());
        PopupWindow popupWindow = new PopupWindow(notificationMoreOptionsBinding.getRoot(), LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, false);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setElevation(50);

        notificationMoreOptionsBinding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteNotification(selectedNotification);
                listNotification.remove(selectedNotification);
                listAllNotification.remove(selectedNotification);
                binding.recyclerviewNotification.setAdapter(notificationAdapter);
                ReloadNotificationView();
                ToastUtils.showToastSuccess(getContext(), "Notification deleted", Toast.LENGTH_SHORT);
                popupWindow.dismiss();
            }
        });

        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        int xOffset = anchor.getWidth(); // Offset from the right edge of the anchor view
        int yOffset = - anchor.getHeight() / 2;
        popupWindow.showAsDropDown(anchor, xOffset, yOffset);
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
                ToastUtils.showToastSuccess(getContext(), "notification deleted", Toast.LENGTH_SHORT);
            }

            @Override
            public void onFailure(String message) {
                ToastUtils.showToastError(getContext(), message, Toast.LENGTH_SHORT);
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
        int color = getResources().getColor(R.color.white, getActivity().getTheme());
        binding.btnAllNotification.setTextColor(color);
    }

    private void DeleteFriendRequest(Notification notificationOfFriendRequest){
        String replierId = SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID);
        String requestSender = notificationOfFriendRequest.getSenderId();
        friendViewModel.reply(replierId, requestSender, "Deny", new FriendViewModel.FriendRequestCallback() {
            @Override
            public void onSuccess() {
                ToastUtils.showToastSuccess(getContext(), "you have just denied the request!!", Toast.LENGTH_SHORT);
            }

            @Override
            public void onFailure(String message) {
                ToastUtils.showToastError(getContext(), message, Toast.LENGTH_SHORT);
            }
        });
    }
}