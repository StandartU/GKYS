package com.example.walkiepaws.backend.model;
import java.util.List;
import lombok.Setter;
import lombok.Getter;


@Setter
@Getter
public class ItemModel {

    private int id;

    private String name;

    private int price;

    private List<PetItemModel> petItems;

    private List<UserItemModel> userItems;
}