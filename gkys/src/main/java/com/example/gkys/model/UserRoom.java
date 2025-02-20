package com.example.gkys.model;
import jakarta.persistence.*;

@Entity
@Table(name = "user_room")
public class UserRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room roomId;

    private int lvl;

    // Getters and Setters
}