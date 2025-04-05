package com.example.walkiepaws.backend.model;

import java.util.List;

public class RoomLvlModel {
    private int id;
    private RoomModel room;
    private int lvl;
    private List<String> templates;
    private int price;

    // Пустой конструктор
    public RoomLvlModel() {}

    // Полный конструктор
    public RoomLvlModel(int id, RoomModel room, int lvl, List<String> templates, int price) {
        this.id = id;
        this.room = room;
        this.lvl = lvl;
        this.templates = templates;
        this.price = price;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public List<String> getTemplates() {
        return templates;
    }

    public void setTemplates(List<String> templates) {
        this.templates = templates;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
