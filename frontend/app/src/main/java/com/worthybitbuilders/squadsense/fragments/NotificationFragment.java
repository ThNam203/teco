package com.worthybitbuilders.squadsense.fragments;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.worthybitbuilders.squadsense.MainActivity;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.activities.FriendRequestActivity;
import com.worthybitbuilders.squadsense.activities.ProjectActivityLogActivity;
import com.worthybitbuilders.squadsense.adapters.NotificationAdapter;
import com.worthybitbuilders.squadsense.adapters.NotificationOptionViewAdapter;
import com.worthybitbuilders.squadsense.databinding.FragmentNotificationBinding;
import com.worthybitbuilders.squadsense.databinding.NotificationMoreOptionsBinding;
import com.worthybitbuilders.squadsense.models.Notification;
import com.worthybitbuilders.squadsense.utils.ActivityUtils;
import com.worthybitbuilders.squadsense.utils.ConvertUtils;
import com.worthybitbuilders.squadsense.utils.SharedPreferencesManager;
import com.worthybitbuilders.squadsense.utils.ToastUtils;
import com.worthybitbuilders.squadsense.viewmodels.FriendViewModel;
import com.worthybitbuilders.squadsense.viewmodels.NotificationViewModel;
import com.worthybitbuilders.squadsense.viewmodels.ProjectActivityViewModel;
import com.worthybitbuilders.squadsense.viewmodels.UserViewModel;

import java.util.ArrayList;
import java.util.Calendar;
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
    private List<String> listOptionView = new ArrayList<>();

    private NotificationOptionViewAdapter adapter;
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

        //Recycler view of optionview
        binding.rvOptionView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new NotificationOptionViewAdapter(listOptionView, MainActivity.getNotificationFragmentOptionView());

        //recycler view of notification view
        binding.recyclerviewNotification.setLayoutManager(new LinearLayoutManager(getContext()));
        notificationAdapter = new NotificationAdapter(listNotification);

        binding.btnSelectDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                listNotification.clear();

                                //get today notification
                                final String selectedDate = ConvertUtils.formatDate(dayOfMonth, monthOfYear + 1, year);
                                for (Notification item : listAllNotification)
                                {
                                    if(item.getTimeCreatedDMY().equals(selectedDate))
                                    {
                                        listNotification.add(item);
                                    }
                                }

                                //reload view of list
                                binding.recyclerviewNotification.setAdapter(notificationAdapter);
                                ReloadNotificationView();

                                listOptionView.add(selectedDate);
                                adapter.setSelectedOption(selectedDate);
                                binding.rvOptionView.setAdapter(adapter);
                                MainActivity.setNotificationFragmentOptionView(selectedDate);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
                datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.GREEN);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        LoadData();
    }

    private void LoadData()
    {
        notificationViewModel.getNotification(SharedPreferencesManager.getData(SharedPreferencesManager.KEYS.USER_ID), new NotificationViewModel.getNotificationCallback() {
            @Override
            public void onSuccess(List<Notification> notificationsData) {
                listNotification.clear();
                listAllNotification.clear();
                listNotification.addAll(notificationsData);
                listNotification.sort(Comparator.comparing(Notification::getTimeCreated).reversed());
                listAllNotification.addAll(listNotification);
                binding.recyclerviewNotification.setAdapter(notificationAdapter);
                ReloadNotificationView();

                setupOptionView();
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

        notificationAdapter.setOnReplyMemberRequest(new NotificationAdapter.ReplyMemberRequestHandler() {
            @Override
            public void OnAccept(int position) {
                String projectId = listNotification.get(position).getLink();
                String senderId = listNotification.get(position).getSenderId();

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
            }

            @Override
            public void OnDeny(int position) {
                String projectId = listNotification.get(position).getLink();
                String senderId = listNotification.get(position).getSenderId();
                String senderName = listNotification.get(position).getTitle();
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
            }
        });

        notificationAdapter.setOnReplyAdminRequest(new NotificationAdapter.ReplyAdminRequestHandler() {
            @Override
            public void OnAccept(int position) {
                String projectId = listNotification.get(position).getLink();
                String senderId = listNotification.get(position).getSenderId();
                String senderName = listNotification.get(position).getTitle();

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
            }

            @Override
            public void OnDeny(int position) {
                String projectId = listNotification.get(position).getLink();
                String senderId = listNotification.get(position).getSenderId();
                String senderName = listNotification.get(position).getTitle();

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
            }
        });
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

    private void setupOptionView()
    {
        listOptionView.clear();
        listOptionView.add("All");
        listOptionView.add("Today");

        String currentOptionView = MainActivity.getNotificationFragmentOptionView();
        if(!listOptionView.contains(currentOptionView)) listOptionView.add(currentOptionView);

        binding.rvOptionView.setAdapter(adapter);

        if(currentOptionView.equals("All")) handlerAllNotificationOptionView();
        else if(currentOptionView.equals("Today")) handlerTodayNotificationOptionView();
        else handlerSelectDateNotificationOptionView(currentOptionView);


        adapter.setOnOptionViewHandler(new NotificationOptionViewAdapter.OptionViewHandler() {
            @Override
            public void AllNotificationHandler() {
                handlerAllNotificationOptionView();
            }

            @Override
            public void TodayNotificationHandler() {
                handlerTodayNotificationOptionView();
            }

            @Override
            public void SelectDateNotificationHandler(String selectedDate) {
                handlerSelectDateNotificationOptionView(selectedDate);
            }
        });
    }

    private void handlerAllNotificationOptionView()
    {
        listNotification.clear();
        listNotification.addAll(listAllNotification);

        binding.recyclerviewNotification.setAdapter(notificationAdapter);
        ReloadNotificationView();

        adapter.setSelectedOption("All");
        binding.rvOptionView.setAdapter(adapter);
        MainActivity.setNotificationFragmentOptionView("All");
    }

    private void handlerTodayNotificationOptionView()
    {
        listNotification.clear();

        //get today notification
        String dateNow = ConvertUtils.DateToString(new Date(), ConvertUtils.Pattern.DAY_MONTH_YEAR);
        for (Notification item : listAllNotification)
        {
            if(item.getTimeCreatedDMY().equals(dateNow))
            {
                listNotification.add(item);
            }
        }


        binding.recyclerviewNotification.setAdapter(notificationAdapter);
        ReloadNotificationView();

        adapter.setSelectedOption("Today");
        binding.rvOptionView.setAdapter(adapter);
        MainActivity.setNotificationFragmentOptionView("Today");
    }

    private void handlerSelectDateNotificationOptionView(String selectedDate)
    {
        listNotification.clear();

        //get today notification
        for (Notification item : listAllNotification)
        {
            if(item.getTimeCreatedDMY().equals(selectedDate))
            {
                listNotification.add(item);
            }
        }

        binding.recyclerviewNotification.setAdapter(notificationAdapter);
        ReloadNotificationView();

        adapter.setSelectedOption(selectedDate);
        binding.rvOptionView.setAdapter(adapter);
        MainActivity.setNotificationFragmentOptionView(selectedDate);
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