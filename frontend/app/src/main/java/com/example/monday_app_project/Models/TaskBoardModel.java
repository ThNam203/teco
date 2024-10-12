package com.example.monday_app_project.Models;

import android.content.Context;
import android.widget.TableLayout;

import java.util.List;
import java.util.UUID;

public class TaskBoardModel {
    private String Id;
    private String taskBoardName = "";
    private List<TaskModel> tasks;
    public TaskBoardModel(String taskBoardName)
    {
        this.Id = UUID.randomUUID().toString();
        this.taskBoardName = taskBoardName;
    }
    public TaskBoardModel(String Id, String taskBoardName)
    {
        this.Id = Id;
        this.taskBoardName = taskBoardName;
    }
    public String getId(){return this.Id;}
    public void setId(String id) {this.Id = id; }

    public String getTaskBoardName() {
        return taskBoardName;
    }

    public void setTaskBoardName(String taskBoardName) {
        this.taskBoardName = taskBoardName;
    }

    public List<TaskModel> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskModel> tasks) {
        this.tasks = tasks;
    }
}
