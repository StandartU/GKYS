package com.example.walkiepaws.backend.model;

public class PetItemModel {
    private int id;
    private PetModel pet;
    private int lvl;
    private ItemModel item;
    private String template;

    // Пустой конструктор
    public PetItemModel() {}

    // Полный конструктор
    public PetItemModel(int id, PetModel pet, int lvl, ItemModel item, String template) {
        this.id = id;
        this.pet = pet;
        this.lvl = lvl;
        this.item = item;
        this.template = template;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public PetModel getPet() {
        return pet;
    }

    public void setPet(PetModel pet) {
        this.pet = pet;
    }

    public int getLvl() {
        return lvl;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }

    public ItemModel getItem() {
        return item;
    }

    public void setItem(ItemModel item) {
        this.item = item;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
}
