package com.example.walkiepaws.backend.model;

import java.util.Date;

public class UserTaskModel {

    private Long id;

    private TasksModel task;

    private UserModel user;

    private int value;

    private Date date;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TasksModel getTask() {
        return task;
    }

    public void setTask(TasksModel task) {
        this.task = task;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}