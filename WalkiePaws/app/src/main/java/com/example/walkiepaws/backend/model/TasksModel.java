package com.example.walkiepaws.backend.model;

import java.util.List;

public class TasksModel {

    private int id;

    private String name;

    private String description;

    private int target;

    private int revard;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public int getRevard() {
        return revard;
    }

    public void setRevard(int revard) {
        this.revard = revard;
    }
}
