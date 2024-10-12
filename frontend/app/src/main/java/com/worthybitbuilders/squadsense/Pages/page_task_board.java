package com.worthybitbuilders.squadsense.Pages;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.worthybitbuilders.squadsense.Models.TaskBoardModel;
import com.worthybitbuilders.squadsense.Models.TaskModel;
import com.worthybitbuilders.squadsense.R;
import com.worthybitbuilders.squadsense.ViewModels.TaskBoardViewModel;

import java.util.Random;

public class page_task_board extends AppCompatActivity {

    ImageButton btnBack;
    LinearLayout taskBoardFrame;
    TaskBoardViewModel taskBoardViewModel;
    private ActivityResultLauncher<Intent> taskMessageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_task_board);
        getSupportActionBar().hide();

        //Init variables here
        btnBack = (ImageButton) findViewById(R.id.btn_back);
        taskBoardFrame = (LinearLayout) findViewById(R.id.taskboard_frame);
        taskBoardViewModel = new ViewModelProvider(this).get(TaskBoardViewModel.class);

        LoadTaskBoards();

        taskMessageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            // anything
                        }
                    }
                }
        );

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page_task_board.super.onBackPressed();
            }
        });

    }

    private void LoadTaskBoards()
    {
        // list taskboard is new and have no data -> create default taskboard with 3 rows
        if(taskBoardViewModel.getTaskBoardListLiveData().getValue() == null)
        {
            taskBoardViewModel.addNewTaskBoard();
        }

        // get data from taskboard livedata
        taskBoardViewModel.getTaskBoardListLiveData().observe(this, taskBoardList -> {
            for(TaskBoardModel taskBoardModel : taskBoardList)
            {
                HorizontalScrollView horizontalScrollView = new HorizontalScrollView(this);
                horizontalScrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                horizontalScrollView.setHorizontalScrollBarEnabled(false);

                TableLayout taskBoard = (TableLayout) getLayoutInflater().inflate(R.layout.taskboard_header, null);
                TextView taskBoardName = (TextView) taskBoard.findViewById(R.id.taskboard_name);

                int color = getRandomColor();
                taskBoardName.setText(taskBoardModel.getTaskBoardName());
                taskBoardName.setTextColor(color);

                horizontalScrollView.addView(taskBoard);
                taskBoardFrame.addView(horizontalScrollView);
                LoadTaskRows(taskBoard, taskBoardModel, color);
            }
        });
    }

    //define function here
    private void LoadTaskRows(TableLayout taskBoard, TaskBoardModel taskBoardModel, int color)
    {
        // list task have no data
        if(!taskBoardViewModel.IsThisTaskBoardHasTasks(taskBoardModel))
        {
            taskBoardViewModel.addNewTask(taskBoardModel);
            taskBoardViewModel.addNewTask(taskBoardModel);
            taskBoardViewModel.addNewTask(taskBoardModel);
        }

        taskBoardViewModel.getTaskListLiveData().observe(this, taskList -> {
            // Delete just all rows in tablelayout view
            removeAllRowsInTableLayout(taskBoard);

            // add row from listdata to UI
            for (TaskModel task : taskList) {
                if(task.getTaskBoardId().equals(taskBoardModel.getId()))
                {
                    // Inflate layout của TableRow
                    TableRow taskRow = (TableRow) getLayoutInflater().inflate(R.layout.taskboard_row, null);
                    LayerDrawable layerDrawable = (LayerDrawable) taskRow.getBackground();

                    // Lấy item đầu tiên trong LayerDrawable và set màu
                    layerDrawable.setDrawableByLayerId(layerDrawable.getId(0), new ColorDrawable(color));

                    // Set lại background cho TableRow
                    taskRow.setBackground(layerDrawable);

                    // Lấy ra các TextView trong layout
                    TextView taskName = taskRow.findViewById(R.id.task_name);
                    TextView taskStatus = taskRow.findViewById(R.id.task_status);
                    TextView taskDate = taskRow.findViewById(R.id.task_date);
                    ImageButton taskMessage = taskRow.findViewById(R.id.task_message);


                    // Thiết lập giá trị cho các TextView
                    ////taskName
                    taskName.setText(String.valueOf(task.getTaskName()));
                    ////taskStatus
                    SetStatusView(taskStatus, task.getStatus());
                    taskStatus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showTaskStatusPopup(taskStatus, task);
                        }
                    });
                    ////taskDate
                    taskDate.setText(task.getDate());

                    // Chuyển đến cửa trang updates
                    taskMessage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getWindow().getContext(), page_task_message.class);
                            intent.putExtra("task", task);
                            taskMessageLauncher.launch(intent);
                        }
                    });

                    // Thêm TableRow vào TableLayout
                    taskBoard.addView(taskRow);
                }
            }
        });

    }

    private void removeAllRowsInTableLayout(TableLayout tableLayout)
    {
        int childCount = tableLayout.getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            View view = tableLayout.getChildAt(i);
            if (view instanceof TableRow) {
                // Kiểm tra xem đây có phải là hàng đầu tiên hay không
                if (i != 0) {
                    tableLayout.removeView(view);
                }
            }
        }
    }
    private void SetStatusView(TextView textView,TaskModel.Status status)
    {
        switch (status)
        {
            case WORKING:
            {
                textView.setText("Working on it");
                textView.setBackgroundColor(getResources().getColor(R.color.orange));
                break;
            }
            case STUCK:
            {
                textView.setText("Stuck");
                textView.setBackgroundColor(getResources().getColor(R.color.light_red));
                break;
            }
            case DONE:
            {
                textView.setText("Done");
                textView.setBackgroundColor(getResources().getColor(R.color.green));
                break;
            }
        }
    }

    private void showTaskStatusPopup(TextView taskStatus, TaskModel taskModel)
    {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_status);

        //init variables here
        ImageButton btnClosePopup = (ImageButton) dialog.findViewById(R.id.btn_close_popup);
        TextView statusWorking = (TextView) dialog.findViewById(R.id.status_working);
        TextView statusStuck = (TextView) dialog.findViewById(R.id.status_stuck);
        TextView statusDone = (TextView) dialog.findViewById(R.id.status_done);

        //set onclick buttons here
        btnClosePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        statusWorking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetStatusView(taskStatus, TaskModel.Status.WORKING);
                taskModel.setStatus(TaskModel.Status.WORKING);
                dialog.dismiss();
            }
        });
        statusStuck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetStatusView(taskStatus, TaskModel.Status.STUCK);
                taskModel.setStatus(TaskModel.Status.STUCK);
                dialog.dismiss();
            }
        });
        statusDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetStatusView(taskStatus, TaskModel.Status.DONE);
                taskModel.setStatus(TaskModel.Status.DONE);
                dialog.dismiss();
            }
        });

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.PopupAnimationBottom;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
    }

    private int getRandomColor() {

        Random random = new Random();
        int red = random.nextInt(255);
        int green = random.nextInt(255);
        int blue = random.nextInt(255);
        int brightness = 200; // Giá trị độ sáng tối đa
        int max = Math.max(Math.max(red, green), blue);
        if (max > brightness) {
            red = (int) ((double) red * ((double) brightness / (double) max));
            green = (int) ((double) green * ((double) brightness / (double) max));
            blue = (int) ((double) blue * ((double) brightness / (double) max));
        }
        int color = Color.rgb(red, green, blue);

        return color;
    }
}