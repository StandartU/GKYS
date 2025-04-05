package com.example.walkiepaws.backend.model;

public class MarketModel {
    private int id;
    private StateModel state;
    private String name;
    private int price;
    private int value;

    // Пустой конструктор
    public MarketModel() {}

    // Полный конструктор
    public MarketModel(int id, StateModel state, String name, int price, int value) {
        this.id = id;
        this.state = state;
        this.name = name;
        this.price = price;
        this.value = value;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public StateModel getState() {
        return state;
    }

    public void setState(StateModel state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
