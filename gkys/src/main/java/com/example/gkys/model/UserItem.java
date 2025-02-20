package com.example.gkys.model;
import jakarta.persistence.*;

@Entity
@Table(name = "user_item")
public class UserItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item itemId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

    private boolean active;

    // Getters and Setters
}