package com.worthybitbuilders.squadsense.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.databinding.ActivityReportBinding;
import com.worthybitbuilders.squadsense.models.report_models.CheckBox;
import com.worthybitbuilders.squadsense.models.report_models.StatusReportItemModel;
import com.worthybitbuilders.squadsense.models.report_models.StatusReportModel;
import com.worthybitbuilders.squadsense.models.report_models.TimelineModel;
import com.worthybitbuilders.squadsense.models.report_models.UserReportItemModel;
import com.worthybitbuilders.squadsense.models.report_models.UserReportModel;

import java.util.ArrayList;
import java.util.Objects;


public class ReportActivity extends AppCompatActivity {

    private CheckBox mockCheckBoxData = new CheckBox("Check box title", 1 ,3);

    private ArrayList<UserReportModel> mockUserData = new ArrayList<UserReportModel>();

    private TimelineModel mockTimeData = new TimelineModel("Deadlines", new TimelineModel.TimelineValuesModel(0,1,0,3));

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

        renderCheckbox(mockCheckBoxData);

        renderTime(mockTimeData);

        mockUserData.add(new UserReportModel("Title 1", new UserReportItemModel("1","2")));
        mockUserData.add(new UserReportModel("Title 2", new UserReportItemModel("4","3")));
        renderUser(mockUserData);

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

    private void renderTime(TimelineModel timeData){
        View newView = inflateLayout(ReportActivity.this, R.layout.report_timeline_view, activityBinding.reportTimeLayout);
        ((TextView) activityBinding.reportTimeTitle).setText(timeData.title);

        TextView before = (TextView) newView.findViewById(R.id.timeline_report_before_count);
        TextView during = (TextView) newView.findViewById(R.id.timeline_report_during_count);
        TextView after = (TextView) newView.findViewById(R.id.timeline_report_after_count);
        TextView undefined = (TextView) newView.findViewById(R.id.timeline_report_undefined_count);

        before.setText(String.valueOf(timeData.values.before));
        during.setText(String.valueOf(timeData.values.during));
        after.setText(String.valueOf(timeData.values.after));
        undefined.setText(String.valueOf(timeData.values.undefinedValue));



        this.activityBinding.reportTimeLayout.addView(newView);
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



    private void renderCheckbox(CheckBox CheckBoxData){
        View newView = inflateLayout(ReportActivity.this, R.layout.report_checkbox_view, activityBinding.reportCheckboxLayout);
        TextView title = (TextView) activityBinding.reportCheckboxTitle;

        title.setText(CheckBoxData.title);
        TextView checked = (TextView) newView.findViewById(R.id.report_checkbox_checked_number);
        TextView unchecked = (TextView) newView.findViewById(R.id.report_checkbox_unchecked_number);

        checked.setText(String.valueOf(CheckBoxData.checked));
        unchecked.setText(String.valueOf(CheckBoxData.unchecked));

        this.activityBinding.reportCheckboxLayout.addView(newView);
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


