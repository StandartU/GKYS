package com.example.walkiepaws.backend.model;

import java.util.Date;

public class StepsModel extends Model {
    private Long id;
    private UserModel user;
    private Date date;
    private int steps;

    // Пустой конструктор
    public StepsModel() {}

    // Полный конструктор
    public StepsModel(Long id, UserModel user, Date date, int steps) {
        this.id = id;
        this.user = user;
        this.date = date;
        this.steps = steps;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }
}
