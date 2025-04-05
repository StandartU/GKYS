package com.example.walkiepaws.backend.model;

public class UserStateModel {
    private Long id;
    private UserModel user;
    private StateModel state;
    private int value;

    // Пустой конструктор
    public UserStateModel() {}

    public UserStateModel(Long id, UserModel user, StateModel state, int value) {
        this.id = id;
        this.user = user;
        this.state = state;
        this.value = value;
    }

    // Геттеры и Сеттеры
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

    public StateModel getState() {
        return state;
    }

    public void setState(StateModel state) {
        this.state = state;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
