package com.example.gkys.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_item")
@Setter
@Getter
public class UserItemModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private ItemModel item;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModel user;

    private boolean active;

    private int lvl;

    public UserItemModel(ItemModel itemModel, UserModel userModel) {
        this.item = itemModel;
        this.user = userModel;
        this.active = false;
    }

    // Getters and Setters
}