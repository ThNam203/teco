package com.worthybitbuilders.squadsense.Models;

import java.util.ArrayList;
import java.util.List;


public class TaskModel {
    public enum Status {
        WORKING,
        STUCK,
        DONE
    }
    private String taskBoardId;
    private String taskName;
    private List<String> message;
    private UserModel person;
    private Status status;
    private String date;

    public TaskModel()
    {
        message = new ArrayList<String>();
        taskName = "";
        status = Status.WORKING;
        date = "";
    }

    public TaskModel(String taskBoardId, String taskName,Status status, String date)
    {
        message = new ArrayList<String>();
        this.taskBoardId = taskBoardId;
        this.taskName = taskName;
        this.status = status;
        this.date = date;
    }

    public String getTaskBoardId(){return this.taskBoardId;}
    public void setTaskBoardId(String taskBoardId){this.taskBoardId = taskBoardId;}
    public Status getStatus(){
        return status;
    }
    public void setStatus(Status status){this.status = status;}

    public String getTaskName() {return this.taskName;}

    public void setTaskName(String taskName) {this.taskName = taskName; }

    public List<String> getMessage() {
        return message;
    }

    public void setMessage(List<String> message) {
        this.message = message;
    }

    public UserModel getPerson() {
        return person;
    }

    public void setPerson(UserModel person) {
        this.person = person;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
