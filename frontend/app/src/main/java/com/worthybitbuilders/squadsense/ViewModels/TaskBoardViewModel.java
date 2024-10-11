package com.worthybitbuilders.squadsense.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.worthybitbuilders.squadsense.Models.TaskBoardModel;
import com.worthybitbuilders.squadsense.Models.TaskModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TaskBoardViewModel extends ViewModel {
    private MutableLiveData<List<TaskBoardModel>> taskBoardListLiveData = new MutableLiveData<>();
    private MutableLiveData<List<TaskModel>> taskListLiveData = new MutableLiveData<>();
    private List<TaskBoardModel> taskBoardList;
    private List<TaskModel> taskList;


    public TaskBoardViewModel() {

        // Thêm sẵn vài dữ liệu ban đầu
        taskBoardList = new ArrayList<>();
        taskList = new ArrayList<>();
        TaskBoardModel taskBoardModel1 = new TaskBoardModel("Abc");
        TaskBoardModel taskBoardModel2 = new TaskBoardModel("asdlkja");
        TaskBoardModel taskBoardModel3 = new TaskBoardModel("adsjasd");
        TaskBoardModel taskBoardModel4 = new TaskBoardModel("askdjha");
        TaskBoardModel taskBoardModel5 = new TaskBoardModel("ABC");

        TaskModel taskModel1 = new TaskModel(taskBoardModel1.getId(), "add board", TaskModel.Status.DONE, "2/5/2023");
        TaskModel taskModel2 = new TaskModel(taskBoardModel2.getId(), "add task", TaskModel.Status.WORKING, "2/5/2023");
        TaskModel taskModel3 = new TaskModel(taskBoardModel2.getId(), "done taskboard", TaskModel.Status.STUCK, "2/5/2023");

        taskBoardList.add(taskBoardModel1);
        taskBoardList.add(taskBoardModel2);
        taskBoardList.add(taskBoardModel3);
        taskBoardList.add(taskBoardModel4);
        taskBoardList.add(taskBoardModel5);
        taskBoardListLiveData.setValue(taskBoardList);

        taskList.add(taskModel1);
        taskList.add(taskModel2);
        taskList.add(taskModel3);
        taskListLiveData.setValue(taskList);
    }

    public synchronized LiveData<List<TaskBoardModel>> getTaskBoardListLiveData() {
        return taskBoardListLiveData;
    }
    public synchronized LiveData<List<TaskModel>> getTaskListLiveData() {
        return taskListLiveData;
    }

    public void addNewTaskBoard() {
        // Lấy ra danh sách cũ
        taskBoardList = taskBoardListLiveData.getValue();
        if (taskBoardList == null) {
            taskBoardList = new ArrayList<>();
        }
        int taskBoardListSize = taskBoardList.size();
        TaskBoardModel newtaskBoardModel = new TaskBoardModel("Group title " + String.valueOf(taskBoardListSize + 1));
        // Thêm tác vụ mới
        taskBoardList.add(newtaskBoardModel);

        // Cập nhật LiveData
        taskBoardListLiveData.setValue(taskBoardList);
    }

    public void addNewTask(TaskBoardModel taskBoard) {
        String date = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
        // Lấy ra danh sách cũ
        taskList = taskListLiveData.getValue();

        if (taskList == null) {
            taskList = new ArrayList<>();
        }
        TaskModel newtaskModel = new TaskModel(taskBoard.getId(), "Item " + String.valueOf(CountTask(taskBoard) + 1), TaskModel.Status.WORKING, date);

        taskList.add(newtaskModel);
        // Cập nhật LiveData
        taskListLiveData.setValue(taskList);
    }

    public boolean IsThisTaskBoardHasTasks(TaskBoardModel taskBoard)
    {
        for(TaskModel task : taskList)
        {
            if(task.getTaskBoardId().equals(taskBoard.getId()))
                return true;
        }
        return false;
    }

    public int CountTask(TaskBoardModel taskBoard)
    {
        int count = 0;
        for(TaskModel task : taskList)
        {
            if(task.getTaskBoardId().equals(taskBoard.getId()))
                count += 1;
        }
        return count;
    }
}
