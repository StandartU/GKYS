package com.example.walkiepaws.backend.model;

import java.util.List;

public class StateModel {
    private int id;
    private String name;
    private List<MarketModel> markets;
    private List<UserStateModel> userStates;

    // Пустой конструктор
    public StateModel() {}

    // Полный конструктор
    public StateModel(int id, String name, List<MarketModel> markets, List<UserStateModel> userStates) {
        this.id = id;
        this.name = name;
        this.markets = markets;
        this.userStates = userStates;
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

    public List<MarketModel> getMarkets() {
        return markets;
    }

    public void setMarkets(List<MarketModel> markets) {
        this.markets = markets;
    }

    public List<UserStateModel> getUserStates() {
        return userStates;
    }

    public void setUserStates(List<UserStateModel> userStates) {
        this.userStates = userStates;
    }
}
