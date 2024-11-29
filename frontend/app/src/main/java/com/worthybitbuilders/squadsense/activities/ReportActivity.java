package com.worthybitbuilders.squadsense.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.databinding.ActivityReportBinding;
import com.worthybitbuilders.squadsense.models.report_models.CheckBox;
import com.worthybitbuilders.squadsense.models.report_models.ReportModel;
import com.worthybitbuilders.squadsense.models.report_models.StatusReportModel;
import com.worthybitbuilders.squadsense.models.report_models.TimelineModel;
import com.worthybitbuilders.squadsense.models.report_models.UserReportModel;
import com.worthybitbuilders.squadsense.services.ReportService;
import com.worthybitbuilders.squadsense.services.RetrofitServices;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ReportActivity extends AppCompatActivity {

    ReportService reportService = RetrofitServices.getReportService();
    public List<CheckBox> checkBoxData = new ArrayList<CheckBox>();

    private List<TimelineModel> timeData = new ArrayList<TimelineModel>();

    private List<StatusReportModel> statusData = new ArrayList<StatusReportModel>();

    private List<ReportModel> reports = new ArrayList<ReportModel>();

    private int currentReportIndex = 0;

    private ActivityReportBinding activityBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        Objects.requireNonNull(getSupportActionBar()).hide();
        activityBinding = ActivityReportBinding.inflate(getLayoutInflater());
        setContentView(activityBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        activityBinding.reportBtnBack.setOnClickListener((view)->finish());

        Intent intent = getIntent();
        String projectId = intent.getStringExtra("projectId");


        Call<List<ReportModel>> data = reportService.getProjectReport(String.valueOf(projectId));
        data.enqueue(new Callback<List<ReportModel>>() {
            @Override
            public void onResponse(Call<List<ReportModel>> call, Response<List<ReportModel>> response) {
                if(response.isSuccessful()){
                    Gson gson = new Gson();
                    List<ReportModel> reportsList = response.body();
                    if(!reportsList.isEmpty()) reports.addAll(reportsList);

                    // Process the first report if list is not empty
                        if (reportsList != null && !reportsList.isEmpty()) {
                        ReportModel firstReport = reports.get(0);

                        ((TextView)activityBinding.reportSelector).setText(String.valueOf(firstReport.title));

                        // Access specific components
                        checkBoxData.clear();
                        checkBoxData.addAll(firstReport.checkbox);

                        timeData.clear();
                        timeData.addAll(firstReport.timeline);

                        statusData.clear();
                        statusData.addAll(firstReport.status);

                        if(!checkBoxData.isEmpty()) renderCheckbox(checkBoxData);
                        if(!timeData.isEmpty()) renderTime(timeData);
                        if(!statusData.isEmpty()) renderStatus(statusData);
                    }
                }
                else Log.d("response", response.errorBody().toString());
            }

            @Override
            public void onFailure(Call<List<ReportModel>> call, Throwable t) {
                Log.e("response", "Network Error: " + t.getMessage());
            }
        });


        List<UserReportModel.Assignee> assignees1 = new ArrayList<UserReportModel.Assignee>();
        assignees1.add(new UserReportModel.Assignee("Dawng"));
        assignees1.add(new UserReportModel.Assignee("Khoa"));

        List<UserReportModel.Assignee> assignees2 = new ArrayList<UserReportModel.Assignee>();
        assignees2.add(new UserReportModel.Assignee("PhucsBui"));
        assignees2.add(new UserReportModel.Assignee("BTNam"));

        List<UserReportModel.Assignment> assignments = new ArrayList<UserReportModel.Assignment>();
        assignments.add(new UserReportModel.Assignment("Task 1", assignees1));
        assignments.add(new UserReportModel.Assignment("Task 2", assignees2));

        List<UserReportModel> userReport = new ArrayList<UserReportModel>();
        userReport.add(new UserReportModel("Personal", assignments));

        renderUser(userReport);

        activityBinding.reportSelector.setOnClickListener(view->{
            selectorClicked();
        });
    }

    private void renderTime(List<TimelineModel> timeData){
        for (int i = activityBinding.reportTimeLayout.getChildCount() - 1; i >= 2; i--) {
            activityBinding.reportTimeLayout.removeViewAt(i);
        }

        timeData.forEach(time->{
            View newView = inflateLayout(ReportActivity.this, R.layout.report_timeline_view, activityBinding.reportTimeLayout);
            TextView title = (TextView) newView.findViewById(R.id.report_timeline_title);
            title.setText(time.title);


            TextView before = (TextView) newView.findViewById(R.id.report_timeline_before_count);
            TextView during = (TextView) newView.findViewById(R.id.report_timeline_during_count);
            TextView after = (TextView) newView.findViewById(R.id.report_timeline_after_count);
            TextView undefined = (TextView) newView.findViewById(R.id.report_timeline_undefined_count);

            before.setText(String.valueOf(time.values.before));
            during.setText(String.valueOf(time.values.during));
            after.setText(String.valueOf(time.values.after));

            this.activityBinding.reportTimeLayout.addView(newView);
        });
    }

    private void renderStatus(List<StatusReportModel> statusData){
        for (int i = activityBinding.reportStatusLayout.getChildCount() - 1; i >= 2; i--) {
            activityBinding.reportStatusLayout.removeViewAt(i);
        }

        statusData.forEach(item-> {
            View newView = inflateLayout(ReportActivity.this, R.layout.report_status_view, activityBinding.reportStatusLayout);

            TextView title = (TextView) newView.findViewById(R.id.status_report_title);
            title.setText(item.title);


            LinearLayout statusItemsContainer = newView.findViewById(R.id.status_items_container);
            item.statuses.forEach(subItem->{
                View newSubView = inflateLayout(ReportActivity.this, R.layout.report_status_item_view, statusItemsContainer);
                TextView label = (TextView) newSubView.findViewById(R.id.status_report_label1);
                TextView count = (TextView) newSubView.findViewById(R.id.status_report_count1);

                label.setText(String.valueOf(subItem.label));
                label.setBackground(ContextCompat.getDrawable(this, R.drawable.background_btn_corner));
                label.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(subItem.color)));
                count.setText(subItem.count);
                statusItemsContainer.addView(newSubView);
            });

           this.activityBinding.reportStatusLayout.addView(newView);
        });
    }

    private void renderUser(List<UserReportModel> usersData){


        usersData.forEach(user->{
            View newView = inflateLayout(ReportActivity.this, R.layout.report_user_view, activityBinding.reportUserLayout);
            TextView title = newView.findViewById(R.id.report_user_title);
            title.setText(user.title);

            LinearLayout assignmentContainer = newView.findViewById(R.id.report_user_assignment_containter);
            user.assignments.forEach(assignment->{
                View assignmentView = inflateLayout(ReportActivity.this, R.layout.report_user_assignment_view, assignmentContainer);
                TextView label = assignmentView.findViewById(R.id.report_user_assignment_label);
                label.setText(assignment.label);

                LinearLayout assigneeContainer = assignmentView.findViewById(R.id.report_user_assignee_container);
                assignment.user.forEach(name->{
                    View assigneeView = inflateLayout(ReportActivity.this, R.layout.report_user_assignee_view, assigneeContainer);
                    TextView username = assigneeView.findViewById(R.id.report_user_assignee_name);
                    username.setText(name.name);
                    assigneeContainer.addView(assigneeView);
                });

                assignmentContainer.addView(assignmentView);
            });

            this.activityBinding.reportUserLayout.addView(newView);

        });
    }



    private void renderCheckbox(List<CheckBox> CheckBoxData){
        for (int i = activityBinding.reportCheckboxLayout.getChildCount() - 1; i >= 2; i--) {
            activityBinding.reportCheckboxLayout.removeViewAt(i);
        }

        CheckBoxData.forEach(checkBox->{
            View newView = inflateLayout(ReportActivity.this, R.layout.report_checkbox_view, activityBinding.reportCheckboxLayout);
            TextView title = (TextView) newView.findViewById(R.id.report_checkBox_title);
            title.setText(checkBox.title);

            TextView checked = (TextView) newView.findViewById(R.id.report_checkbox_checked_number);
            TextView unchecked = (TextView) newView.findViewById(R.id.report_checkbox_unchecked_number);
            checked.setText(String.valueOf(checkBox.checked));
            unchecked.setText(String.valueOf(checkBox.unchecked));

            this.activityBinding.reportCheckboxLayout.addView(newView);
        });
    }


    private View inflateLayout(Context context, int layoutId, ViewGroup parent) {
        return LayoutInflater
                .from(context)
                .inflate(layoutId, parent, false);
    }

    private void selectorClicked(){

        // Create a dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Prepare the list of report titles for the dialog
        String[] reportTitles = new String[reports.size()];
        for (int i = 0; i < reports.size(); i++) {
            reportTitles[i] = reports.get(i).title;
        }

        // Set the dialog title
        builder.setTitle("Select board");

        // Create a single-choice list dialog
        builder.setSingleChoiceItems(reportTitles, currentReportIndex, (dialog, which) -> {
            // When an item is selected
            if (which != currentReportIndex) {
                // Clear existing data
                clearExistingData();

                // Update to the selected report
                ReportModel selectedReport = reports.get(which);

                // Update UI elements
                updateUIWithReport(selectedReport);

                // Update current index
                currentReportIndex = which;

                // Dismiss dialog
                dialog.dismiss();
            } else {
                // If the same item is selected, just dismiss the dialog
                dialog.dismiss();
            }
        });

        // Add a cancel button
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void clearExistingData() {
        // Clear all existing data lists
        checkBoxData.clear();
        timeData.clear();
        statusData.clear();
    }

    private void updateUIWithReport(ReportModel report) {
        // Update report title
        ((TextView)activityBinding.reportSelector).setText(String.valueOf(report.title));

        // Populate data lists
        checkBoxData.addAll(report.checkbox);
        timeData.addAll(report.timeline);
        statusData.addAll(report.status);

        // Render new data
        renderCheckbox(checkBoxData);
        renderTime(timeData);
        renderStatus(statusData);
    }

}


