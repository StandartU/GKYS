package com.example.walkiepaws.backend.model;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class PetItemModel {

    private int id;

    private PetModel pet;

    private int lvl;

    private ItemModel item;

    private String template;

}