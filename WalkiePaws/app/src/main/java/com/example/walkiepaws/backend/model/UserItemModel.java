package com.example.walkiepaws.backend.model;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class UserItemModel {

    private int id;


    private ItemModel item;


    private UserModel user;

    private boolean active;

    public UserItemModel(ItemModel itemModel, UserModel userModel) {
        this.item = itemModel;
        this.user = userModel;
        this.active = false;
    }

    // Getters and Setters
}