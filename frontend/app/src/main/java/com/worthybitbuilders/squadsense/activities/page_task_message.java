package com.worthybitbuilders.squadsense.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.worthybitbuilders.squadsense.models.TaskModel;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.utils.UpdateAdapter;
import com.worthybitbuilders.squadsense.viewmodels.TaskBoardViewModel;

import java.util.List;

public class page_task_message extends AppCompatActivity {

    UpdateAdapter adapter;
    private ListView lvUpdate;
    private List<String> messList;
    TaskBoardViewModel taskBoardViewModel;

    private void ReloadLV() {
        UpdateAdapter adapter = new UpdateAdapter(this, R.layout.item_message_update, messList);
        lvUpdate.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_task_message);
        getSupportActionBar().hide();
        taskBoardViewModel = new ViewModelProvider(this).get(TaskBoardViewModel.class);


        TextView tvMessName = (TextView) findViewById(R.id.message_name);
        EditText textUpdate = (EditText) findViewById(R.id.message_Edit);
        ImageButton btnBack = (ImageButton) findViewById(R.id.btn_back);
        ImageButton btnSend = (ImageButton) findViewById(R.id.sendMess);

        // get task data
        TaskModel task = (TaskModel) getIntent().getSerializableExtra("task");
        if (task != null) {
            tvMessName.setText(task.getTaskName());
            messList = task.getMessage();

        }


        lvUpdate = (ListView) findViewById(R.id.lv_update_mess);
        ReloadLV();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskBoardViewModel.updateTaskMess(task.getTaskBoardId(), messList);
                finish();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messList.add(0, textUpdate.getText().toString());
                ReloadLV();
                textUpdate.setText("");
            }
        });
    }
}