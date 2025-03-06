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
    private ItemModel itemId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModel userId;

    private boolean active;

    // Getters and Setters
}