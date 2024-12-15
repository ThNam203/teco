package com.worthybitbuilders.squadsense.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.databinding.ActivityReportBinding;
import com.worthybitbuilders.squadsense.models.report_models.StatusReportItemModel;
import com.worthybitbuilders.squadsense.models.report_models.StatusReportModel;
import com.worthybitbuilders.squadsense.models.report_models.UserReportItemModel;
import com.worthybitbuilders.squadsense.models.report_models.UserReportModel;

import java.util.ArrayList;
import java.util.Objects;


public class ReportActivity extends AppCompatActivity {

    private ArrayList<Pair<String, String>> mockCheckBoxData = new ArrayList<Pair<String, String>>();

    private ArrayList<UserReportModel> mockUserData = new ArrayList<UserReportModel>();

    private ArrayList<StatusReportModel> mockTimeData = new ArrayList<StatusReportModel>();

    private ArrayList<StatusReportModel> mockStatusData = new ArrayList<StatusReportModel>();

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

        this.mockCheckBoxData.add(new Pair<String, String>("Checked", "2"));
        this.mockCheckBoxData.add(new Pair<String, String>("Unchecked", "3"));
        renderCheckbox(mockCheckBoxData);

        mockUserData.add(new UserReportModel("Title 1", new UserReportItemModel("1","2")));
        mockUserData.add(new UserReportModel("Title 2", new UserReportItemModel("4","3")));
        renderUser(mockUserData);

        ArrayList<StatusReportItemModel> time1Arr = new ArrayList<StatusReportItemModel>();
        time1Arr.add(new StatusReportItemModel("Done", "1"));
        time1Arr.add(new StatusReportItemModel("Working", "2"));

        mockTimeData.add(
                new StatusReportModel("Time 1", time1Arr)
        );

        ArrayList<StatusReportItemModel> time2Arr = new ArrayList<StatusReportItemModel>();
        time2Arr.add(new StatusReportItemModel("Start working", "1"));
        time2Arr.add(new StatusReportItemModel("Working", "2"));
        mockTimeData.add(
                new StatusReportModel("Time 2", time2Arr)

        );
        renderStatus(mockTimeData, activityBinding.reportTimeLayout);

        ArrayList<StatusReportItemModel> status1Arr = new ArrayList<StatusReportItemModel>();
        status1Arr.add(new StatusReportItemModel("Done", "4"));
        status1Arr.add(new StatusReportItemModel("Working", "1"));

        mockStatusData.add(
                new StatusReportModel("Frontend", status1Arr)
        );

        ArrayList<StatusReportItemModel> status2Arr = new ArrayList<StatusReportItemModel>();
        status2Arr.add(new StatusReportItemModel("Done", "1"));
        status2Arr.add(new StatusReportItemModel("Late", "2"));
        mockStatusData.add(
                new StatusReportModel("Backend", status2Arr)

        );
        renderStatus(mockStatusData, activityBinding.reportStatusLayout);

    }

    private void renderStatus(ArrayList<StatusReportModel> statusData, ViewGroup parent){
        statusData.forEach(item-> {
            View newView = inflateLayout(ReportActivity.this, R.layout.report_status_view, parent);

            TextView title = (TextView) newView.findViewById(R.id.status_report_title);
            title.setText(item.title);


            LinearLayout statusItemsContainer = newView.findViewById(R.id.status_items_container);
            item.status.forEach(subItem->{
                View newSubView = inflateLayout(ReportActivity.this, R.layout.report_status_item_view, statusItemsContainer);
                TextView label = (TextView) newSubView.findViewById(R.id.status_report_label1);
                TextView count = (TextView) newSubView.findViewById(R.id.status_report_count1);

                label.setText(subItem.label);
                count.setText(subItem.statusCount);
                statusItemsContainer.addView(newSubView);
            });

           parent.addView(newView);
        });
    }



    private void renderCheckbox(ArrayList<Pair<String, String>> CheckBoxData){
        CheckBoxData.forEach(item-> {
            View newView = inflateLayout(ReportActivity.this, R.layout.report_checkbox_view, activityBinding.reportCheckboxLayout);

            TextView label = (TextView) newView.findViewById(R.id.report_checkbox_label);
            TextView number = (TextView) newView.findViewById(R.id.report_checkbox_number);
            ImageView icon = (ImageView) newView.findViewById(R.id.report_checkbox_icon);

            label.setText(item.first);
            number.setText(item.second);
            icon.setImageResource(item.first.equals("Checked")?R.drawable.ic_checkbox_checked:R.drawable.ic_checkbox_unchecked);

            this.activityBinding.reportCheckboxLayout.addView(newView);
        });
    }

    private void renderUser(ArrayList<UserReportModel> mockUserData){
        mockUserData.forEach(item->{
            View newView = inflateLayout(ReportActivity.this, R.layout.report_user_view, activityBinding.reportUserLayout);
            TextView title = (TextView) newView.findViewById(R.id.report_user_title);
            title.setText(item.title);
            TextView assign = (TextView) newView.findViewById(R.id.report_user_assigned_number);
            assign.setText(item.item.assigned);
            TextView unassign = (TextView) newView.findViewById(R.id.report_user_unassigned_number);
            unassign.setText(item.item.unassigned);
            this.activityBinding.reportUserLayout.addView(newView);
        });
    }

    private View inflateLayout(Context context, int layoutId, ViewGroup parent) {
        return LayoutInflater
                .from(context)
                .inflate(layoutId, parent, false);
    }

}


