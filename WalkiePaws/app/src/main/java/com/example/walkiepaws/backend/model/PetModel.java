package com.example.walkiepaws.backend.model;

import java.util.List;

public class PetModel {
    private int id;
    private String name;
    private List<PetStateModel> petStates;
    private List<PetItemModel> petItems;

    // Пустой конструктор
    public PetModel() {}

    // Полный конструктор
    public PetModel(int id, String name, List<PetStateModel> petStates, List<PetItemModel> petItems) {
        this.id = id;
        this.name = name;
        this.petStates = petStates;
        this.petItems = petItems;
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

    public List<PetStateModel> getPetStates() {
        return petStates;
    }

    public void setPetStates(List<PetStateModel> petStates) {
        this.petStates = petStates;
    }

    public List<PetItemModel> getPetItems() {
        return petItems;
    }

    public void setPetItems(List<PetItemModel> petItems) {
        this.petItems = petItems;
    }
}
