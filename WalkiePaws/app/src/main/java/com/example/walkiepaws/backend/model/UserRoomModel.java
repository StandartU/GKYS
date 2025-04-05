package com.example.walkiepaws.backend.model;

public class UserRoomModel {
    private int id;
    private UserModel user;
    private RoomModel room;
    private int lvl;

    // Пустой конструктор
    public UserRoomModel() {}

    // Полный конструктор
    public UserRoomModel(int id, UserModel user, RoomModel room, int lvl) {
        this.id = id;
        this.user = user;
        this.room = room;
        this.lvl = lvl;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public RoomModel getRoom() {
        return room;
    }

    public void setRoom(RoomModel room) {
        this.room = room;
    }

    public int getLvl() {
        return lvl;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }
}
