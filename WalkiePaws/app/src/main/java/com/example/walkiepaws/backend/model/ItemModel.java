package com.example.walkiepaws.backend.model;

import java.util.List;

public class ItemModel {

    private int id;
    private String name;
    private int price;

    private String surname;

    private String category;

    private List<PetItemModel> petItems;

    private List<UserItemModel> userItems;

    // Пустой конструктор
    public ItemModel() {}

    // Полный конструктор
    public ItemModel(int id, String name, int price, List<PetItemModel> petItems, List<UserItemModel> userItems, String surname, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.petItems = petItems;
        this.userItems = userItems;
        this.surname = surname;
        this.category = category;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public String getSurname() {
        return surname;
    }

    public String getCategory() {
        return category;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public List<PetItemModel> getPetItems() {
        return petItems;
    }

    public void setPetItems(List<PetItemModel> petItems) {
        this.petItems = petItems;
    }

    public List<UserItemModel> getUserItems() {
        return userItems;
    }

    public void setUserItems(List<UserItemModel> userItems) {
        this.userItems = userItems;
    }
}
