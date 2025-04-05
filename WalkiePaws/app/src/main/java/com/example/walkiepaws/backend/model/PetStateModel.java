package com.example.walkiepaws.backend.model;

import java.util.List;

public class PetStateModel {
    private int id;
    private int lvl;
    private List<PetStateTemplatesModel> template;
    private PetModel pet;

    // Пустой конструктор
    public PetStateModel() {}

    // Полный конструктор
    public PetStateModel(int id, int lvl, List<PetStateTemplatesModel> template, PetModel pet) {
        this.id = id;
        this.lvl = lvl;
        this.template = template;
        this.pet = pet;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLvl() {
        return lvl;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }

    public List<PetStateTemplatesModel> getTemplate() {
        return template;
    }

    public void setTemplate(List<PetStateTemplatesModel> template) {
        this.template = template;
    }

    public PetModel getPet() {
        return pet;
    }

    public void setPet(PetModel pet) {
        this.pet = pet;
    }
}
