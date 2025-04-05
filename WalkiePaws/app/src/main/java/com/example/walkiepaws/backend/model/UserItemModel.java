package com.example.walkiepaws.backend.model;

public class UserItemModel {
    private int id;
    private ItemModel item;
    private UserModel user;
    private boolean active;

    // Пустой конструктор
    public UserItemModel() {}

    // Полный конструктор
    public UserItemModel(int id, ItemModel item, UserModel user, boolean active) {
        this.id = id;
        this.item = item;
        this.user = user;
        this.active = active;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ItemModel getItem() {
        return item;
    }

    public void setItem(ItemModel item) {
        this.item = item;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
