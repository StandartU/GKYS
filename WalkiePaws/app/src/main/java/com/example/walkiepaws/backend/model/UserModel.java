package com.example.walkiepaws.backend.model;

import java.util.List;

public class UserModel extends Model {
    private Long id;
    private String login;
    private String password;
    private int cash;
    private List<StepsModel> steps;
    private List<UserItemModel> userItems;
    private List<UserRoomModel> userRooms;

    // Пустой конструктор
    public UserModel() {}

    // Полный конструктор
    public UserModel(Long id, String login, String password, int cash,
                     List<StepsModel> steps, List<UserItemModel> userItems,
                     List<UserRoomModel> userRooms) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.cash = cash;
        this.steps = steps;
        this.userItems = userItems;
        this.userRooms = userRooms;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getCash() {
        return cash;
    }

    public void setCash(int cash) {
        this.cash = cash;
    }

    public List<StepsModel> getSteps() {
        return steps;
    }

    public void setSteps(List<StepsModel> steps) {
        this.steps = steps;
    }

    public List<UserItemModel> getUserItems() {
        return userItems;
    }

    public void setUserItems(List<UserItemModel> userItems) {
        this.userItems = userItems;
    }

    public List<UserRoomModel> getUserRooms() {
        return userRooms;
    }

    public void setUserRooms(List<UserRoomModel> userRooms) {
        this.userRooms = userRooms;
    }
}
