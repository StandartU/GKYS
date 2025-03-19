package com.example.gkys.model;
import java.util.List;
import lombok.Setter;
import lombok.Getter;

import jakarta.persistence.*;


@Entity
@Table(name = "item")
@Setter
@Getter
public class ItemModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private int price;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<PetItemModel> petItems;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<UserItemModel> userItems;
}