package com.example.walkiepaws.backend.model;

public class PetStateTemplatesModel {
    private int id;
    private String stateName;
    private PetStateModel petState;

    // Пустой конструктор
    public PetStateTemplatesModel() {}

    // Полный конструктор
    public PetStateTemplatesModel(int id, String stateName, PetStateModel petState) {
        this.id = id;
        this.stateName = stateName;
        this.petState = petState;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public PetStateModel getPetState() {
        return petState;
    }

    public void setPetState(PetStateModel petState) {
        this.petState = petState;
    }
}
