package com.example.monday_app_project.Fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.monday_app_project.Pages.page_edit_profile;
import com.example.monday_app_project.Pages.page_inbox;
import com.example.monday_app_project.Pages.page_myteam;
import com.example.monday_app_project.Pages.page_notification_setting;
import com.example.monday_app_project.Pages.page_profile;
import com.example.monday_app_project.Pages.page_search_everywhere;
import com.example.monday_app_project.R;
import com.example.monday_app_project.Util.SwitchActivity;

public class MoreFragment extends Fragment {
    LinearLayout btnNotificationSetting = null;
    LinearLayout btnInbox = null;
    LinearLayout btnMyteam = null;

    LinearLayout btnSearchEverywhere = null;
    LinearLayout ProfileArea = null;
    Button btnProfile = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_more, container, false);

        btnNotificationSetting = (LinearLayout) v.findViewById(R.id.btn_notification_settings);
        btnInbox = (LinearLayout) v.findViewById(R.id.btn_inbox);
        btnMyteam = (LinearLayout) v.findViewById(R.id.btn_myteam);
        btnSearchEverywhere = (LinearLayout) v.findViewById(R.id.btn_search_everywhere);
        btnProfile = (Button) v.findViewById(R.id.btn_profile);
        ProfileArea = (LinearLayout) v.findViewById(R.id.profile);

        btnNotificationSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnNotificationSetting_showActivity();
            }
        });

        btnInbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnInbox_showActivity();
            }
        });

        btnMyteam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnMyteam_showActivity();
            }
        });

        btnSearchEverywhere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSearchEverywhere_showActivity();
            }
        });
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnProfile_showActivity();
            }
        });
        ProfileArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnProfile_showActivity();
            }
        });

        return v;
    }

    private void btnSearchEverywhere_showActivity() {
        SwitchActivity.switchToActivity(getContext(), page_search_everywhere.class);
    }

    private void btnProfile_showActivity() {
        SwitchActivity.switchToActivity(getContext(), page_profile.class);
    }

    private void btnMyteam_showActivity() {
        SwitchActivity.switchToActivity(getContext(), page_myteam.class);
    }

    private void btnInbox_showActivity() {
        SwitchActivity.switchToActivity(getContext(), page_inbox.class);
    }

    private void btnNotificationSetting_showActivity() {
        SwitchActivity.switchToActivity(getContext(), page_notification_setting.class);
    }

}