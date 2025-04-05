package com.example.walkiepaws.backend.model;

import java.util.List;

public class RoomModel {
    private int id;
    private String name;
    private List<RoomLvlModel> roomLvls;
    private List<UserRoomModel> userRooms;

    // Пустой конструктор
    public RoomModel() {}

    // Полный конструктор
    public RoomModel(int id, String name, List<RoomLvlModel> roomLvls, List<UserRoomModel> userRooms) {
        this.id = id;
        this.name = name;
        this.roomLvls = roomLvls;
        this.userRooms = userRooms;
    }

    // Геттеры и сеттеры
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

    public List<RoomLvlModel> getRoomLvls() {
        return roomLvls;
    }

    public void setRoomLvls(List<RoomLvlModel> roomLvls) {
        this.roomLvls = roomLvls;
    }

    public List<UserRoomModel> getUserRooms() {
        return userRooms;
    }

    public void setUserRooms(List<UserRoomModel> userRooms) {
        this.userRooms = userRooms;
    }
}
