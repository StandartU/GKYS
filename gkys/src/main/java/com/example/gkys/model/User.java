package com.example.gkys.model;

import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String login;
    private String password;
    private int cash;

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL)
    private List<Steps> steps;

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL)
    private List<UserState> userState;

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL)
    private List<UserItem> userItem;

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL)
    private List<UserRoom> userRoom;



    // Getters and Setters
}